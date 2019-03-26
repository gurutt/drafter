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
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Function;

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

        Map<String, List<PlayerData>> grouped;

        try (java.util.stream.Stream<Path> paths = Files.walk(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("rates_football/")).toURI()))) {


            Stream<Path> files = Stream.ofAll(paths);
            grouped = files
                    .filter(Files::isRegularFile)
                    .sorted()
                    .reverse()
                    .take(3)
                    .map(Path::toFile)
                    .map(readData())
                    .toList()
                    .flatMap(f -> f).removeAll(p -> p.getFootball() == null || p.getFootball().getAttributes() == null)
                    .groupBy(PlayerData::getSlug);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        for (Tuple2<String, List<PlayerData>> tuple2 : grouped) {
            PlayerData player = tuple2._2.get(0);
            Double average = tuple2._2.map(p -> p.getFootball().getAttributes().getSkill()).average().get();
            player.setFootball(new PlayerData.Football(new PlayerData.Attributes(average)));

            mongo.save(player);
        }
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

