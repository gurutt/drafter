package org.gurutt.drafter.config;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import org.gurutt.drafter.domain.Player;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class TestMongoConfiguration {

    @Bean
    public MongoMappingContext mongoMappingContext() {
        MongoMappingContext mappingContext = new MongoMappingContext();
        Set<Class<?>> initialEntitySet = new HashSet<Class<?>>();
        initialEntitySet.add(Player.class);

        mappingContext.setInitialEntitySet(initialEntitySet);

        return mappingContext;
    }

    @Bean
    public MappingMongoConverter mongoConverter(MongoMappingContext mongoMappingContext, SimpleMongoDbFactory mongoFactory) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoFactory);
        return new MappingMongoConverter(dbRefResolver, mongoMappingContext);
    }

    @Bean
    public SimpleMongoDbFactory mongoDbFactory(MongoClient mongo) {
        return new SimpleMongoDbFactory(mongo, "test1");
    }

    @Bean
    public MongoClient mongoClient(MongodProcess mongodProcess) throws UnknownHostException {
        Net net = mongodProcess.getConfig().net();
        return new MongoClient(net.getServerAddress().getHostName(), net.getPort());
    }


    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {

        return new MongoTemplate(mongoClient, "test1");
    }

    @Bean(destroyMethod = "stop")
    public MongodProcess mongodProcess(MongodExecutable mongodExecutable) throws IOException {
        return mongodExecutable.start();
    }

    @Bean(destroyMethod = "stop")
    public MongodExecutable mongodExecutable(MongodStarter mongodStarter, IMongodConfig iMongodConfig) {
        return mongodStarter.prepare(iMongodConfig);
    }

    @Bean
    public IMongodConfig mongodConfig() throws IOException {
        return new MongodConfigBuilder().version(Version.Main.PRODUCTION).build();
    }

    @Bean
    public MongodStarter mongodStarter() {
        return MongodStarter.getDefaultInstance();
    }
}

