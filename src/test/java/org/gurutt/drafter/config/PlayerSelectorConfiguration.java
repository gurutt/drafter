package org.gurutt.drafter.config;

import org.gurutt.drafter.service.Drafter;
import org.gurutt.drafter.service.LineUpEngine;
import org.gurutt.drafter.service.PartitionDrafter;
import org.gurutt.drafter.service.PlayerSelector;
import org.gurutt.drafter.service.SlotSwapDrafter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

@ContextConfiguration
@Import(TestMongoConfiguration.class)
public class PlayerSelectorConfiguration {

    @Bean
    public PlayerSelector playerSelector(MongoTemplate mongoTemplate, LineUpEngine lineUpEngine) {
        return new PlayerSelector(mongoTemplate, lineUpEngine);
    }

    @Bean
    public LineUpEngine lineUpEngine(Map<String, Drafter> drafters) {
        return new LineUpEngine(drafters);
    }
    @Bean
    public Map<String, Drafter> drafters() {
        Map<String, Drafter> drafters = new HashMap<>();
        drafters.put("slot-swap", new SlotSwapDrafter());
        return drafters;
    }
}
