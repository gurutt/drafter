package org.gurutt.drafter.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gurutt.drafter.domain.PlayerData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

@Order(1)
@Component
public class DbInitializer implements ApplicationRunner {
    private static final String INIT_DATA_FILE = "players.json";

    private final MongoTemplate mongo;

    @Autowired
    public DbInitializer(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    @Override
    public void run(ApplicationArguments args) {
        loadMainConfiguration();
    }

    private void loadMainConfiguration() {
/*        if (mongo.count(new Query(), PlayerData.COLLECTION) != 0) {
            return;
        }*/

        Resource resource = new ClassPathResource(INIT_DATA_FILE);
        if (resource.exists()) {
            try (InputStream stream = resource.getInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                PlayerData[] players = mapper.readValue(stream, PlayerData[].class);
                for (PlayerData player : players) {
                    mongo.save(player);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}

