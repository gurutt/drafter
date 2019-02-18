package org.gurutt.drafter.telegrambot.processor;

import org.gurutt.drafter.config.PlayerSelectorConfiguration;
import org.gurutt.drafter.domain.PlayerData;
import org.gurutt.drafter.service.PlayerSelector;
import org.gurutt.drafter.service.TestPlayerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = PlayerSelectorConfiguration.class)
@ExtendWith(SpringExtension.class)
class PlayerListMessageProcessorTest {

    @Autowired
    private PlayerSelector playerSelector;
    @Autowired
    private MongoTemplate mongoTemplate;

    private PlayerListMessageProcessor messageProcessor;

    @BeforeEach
    void setUp() {
        messageProcessor = new PlayerListMessageProcessor(playerSelector);
    }

    @Test
    void handle() {

        // given
        mongoTemplate.dropCollection(PlayerData.class);
        mongoTemplate.save(buildPlayer("yura"));
        mongoTemplate.save(buildPlayer("valik"));
        mongoTemplate.save(buildPlayer("roma"));
        mongoTemplate.save(buildPlayer("reus"));

        // when
        Update update = TestPlayerData.pullPlayerListUpdate();
        String reply = messageProcessor.handle(update.getMessage());

        // then
        assertTrue(reply.contains("yura"));
        assertTrue(reply.contains("valik"));
        assertTrue(reply.contains("roma"));
        assertTrue(reply.contains("reus"));
    }

    private PlayerData buildPlayer(String slug) {
        PlayerData playerData = new PlayerData();
        playerData.setSlug(slug);
        playerData.setBasketball(new PlayerData.Basketball(new PlayerData.Attributes(1,2)));
        return playerData;
    }
}