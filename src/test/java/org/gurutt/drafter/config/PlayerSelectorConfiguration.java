package org.gurutt.drafter.config;

import org.gurutt.drafter.service.LineUpEngine;
import org.gurutt.drafter.service.PlayerSelector;
import org.gurutt.drafter.service.SlotSwapDrafter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
@Import(TestMongoConfiguration.class)
public class PlayerSelectorConfiguration {

    @Bean
    public PlayerSelector playerSelector(MongoTemplate mongoTemplate, LineUpEngine lineUpEngine) {
        return new PlayerSelector(mongoTemplate, lineUpEngine);
    }

    @Bean
    public LineUpEngine lineUpEngine() {
        return new LineUpEngine(new SlotSwapDrafter());
    }
}
