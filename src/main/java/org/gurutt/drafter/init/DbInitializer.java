package org.gurutt.drafter.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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

import static org.gurutt.drafter.domain.PlayerData.*;

@Order(1)
@Component
public class DbInitializer implements ApplicationRunner {

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
        //mongo.dropCollection(PlayerData.class);
        if (mongo.count(new Query(), PlayerData.COLLECTION) != 0) {
            return;
        }
        //loadData("rates_football/", FOOTBALL);
        //loadData("rates_basketball/", BASKETBALL);
        loadData("rates_dota/", DOTA);
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
                    .take(10)
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
        switch (sportType) {
            case PlayerData.BASKETBALL: {
                Double average = tuple2._2.map(p -> p.getBasketball().getAttributes().getSkill()).average().get();
                toUpdate.setBasketball(new Basketball(new Attributes(average)));
                break;
            }
            case FOOTBALL: {
                Double average = tuple2._2.map(p -> p.getFootball().getAttributes().getSkill()).average().get();
                toUpdate.setFootball(new Football(new Attributes(average)));
                break;
            }
            case DOTA: {
                Attributes attributes = player.getDota().getAttributes();
                if (attributes.getSkill() == 0.0) {
                    attributes.setSkill(getDotaRank(player));
                }
                break;
            }
        }
        return toUpdate;
    }

    @SneakyThrows
    private Integer getDotaRank(PlayerData player) {
        String dotaStatsUrl
                = "https://api.opendota.com/api/players/" + player.getExtId();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(dotaStatsUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(result);
                    JsonNode rank = root.path("competitive_rank");
                    JsonNode soloRank = root.path("solo_competitive_rank");
                    if (!rank.isNull()) {
                        //return rank.asInt();
                    } else if (!soloRank.isNull()) {
                        ///return soloRank.asInt();
                    }
                    JsonNode mmrEstimate = root.path("mmr_estimate");
                    if (!mmrEstimate.isNull()) {
                        if (!mmrEstimate.path("estimate").isNull()) {
                            return mmrEstimate.path("estimate").asInt();
                        }
                    }
                }
            }
        }

        return 0;
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

