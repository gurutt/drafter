package org.gurutt.drafter.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import org.gurutt.drafter.domain.PlayerData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

import static org.gurutt.drafter.domain.PlayerData.BASKETBALL;
import static org.gurutt.drafter.domain.PlayerData.FOOTBALL;

@Order(1)
@Component
public class DbInitializer implements ApplicationRunner {
    private static final String INIT_DATA_FILE = "players_v1.json";

    private final MongoTemplate mongo;
    private final ObjectMapper mapper;


    @Autowired
    public DbInitializer(MongoTemplate mongo, ObjectMapper mapper) {
        this.mongo = mongo;
        this.mapper = mapper;
    }

    @Override
    public void run(ApplicationArguments args) throws URISyntaxException {
        loadMainConfiguration();
    }

    private void loadMainConfiguration() throws URISyntaxException {
        if (mongo.count(new Query(), PlayerData.COLLECTION) != 0) {
            return;
        }
        loadData("rates_football/", FOOTBALL);
        loadData("rates_basketball/", BASKETBALL);
    }

    private void loadData(String dir, String sportType) throws URISyntaxException {
        Map<String, List<PlayerData>> grouped;

        try (java.util.stream.Stream<Path> paths = Files.walk(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(dir)).toURI()))) {

            Stream<Path> files = Stream.ofAll(paths);
            grouped = files
                    .filter(Files::isRegularFile)
                    .sorted()
                    .reverse()
                    .take(5)
                    .map(Path::toFile)
                    .map(readData())
                    .toList()
                    .flatMap(f -> f)
                    .sorted(Comparator.comparing(PlayerData::getSlug))
                    .groupBy(PlayerData::getSlug);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        for (Tuple2<String, List<PlayerData>> tuple2 : grouped) {
            PlayerData player = getPlayerToSave(tuple2, sportType);
            mongo.save(player);
        }
    }

    private PlayerData getPlayerToSave(Tuple2<String, List<PlayerData>> tuple2, String sportType) {

        PlayerData player = tuple2._2.get(0);
        PlayerData toUpdate = null;

        Query query = new Query();
        query.addCriteria(Criteria.where("slug").is(player.getSlug()));
        PlayerData one = mongo.findOne(query, PlayerData.class);

        if (one != null) {
            toUpdate = one;
        } else {
            toUpdate = player;
        }
        if (sportType.equals(PlayerData.BASKETBALL)) {
            Double average = tuple2._2.map(p -> p.getBasketball().getAttributes().getSkill()).average().get();
            toUpdate.setBasketball(new PlayerData.Basketball(new PlayerData.Attributes(average)));
        } else if (sportType.equals(FOOTBALL)) {
            Double average = tuple2._2.map(p -> p.getFootball().getAttributes().getSkill()).average().get();
            toUpdate.setFootball(new PlayerData.Football(new PlayerData.Attributes(average)));
        }
        return toUpdate;
    }

    private Function<File, List<PlayerData>> readData() {
        return f -> {
            try {
                return mapper.readValue(f, new TypeReference<List<PlayerData>>() {
                });
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }
}

