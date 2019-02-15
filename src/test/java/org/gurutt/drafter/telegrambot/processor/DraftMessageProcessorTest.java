package org.gurutt.drafter.telegrambot.processor;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import org.gurutt.drafter.config.PlayerSelectorConfiguration;
import org.gurutt.drafter.domain.PlayerData;
import org.gurutt.drafter.domain.PlayerData.Attributes;
import org.gurutt.drafter.domain.PlayerData.Basketball;
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
class DraftMessageProcessorTest {

    @Autowired
    private PlayerSelector playerSelector;
    @Autowired
    private MongoTemplate mongoTemplate;

    private DraftMessageProcessor messageProcessor;

    @BeforeEach
    void setUp() {
        messageProcessor = new DraftMessageProcessor(playerSelector);
    }

    @Test
    void parseCmd() {

        Tuple2<List<String>, String> result = messageProcessor.parseCmd("/draft one, two, three");

        assertIterableEquals(result._1, List.of("one","two","three"));
        assertNull(result._2);

        Tuple2<List<String>, String> result1 = messageProcessor.parseCmd("/draft one, two, three | football");

        assertIterableEquals(result1._1, List.of("one","two","three"));
        assertEquals("football", result1._2);

    }

    @Test
    void match() {
        assertTrue(messageProcessor.match(TestPlayerData.pullUpdate()));
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
        Update update = TestPlayerData.pullUpdate();
        String reply = messageProcessor.handle(update.getMessage());

        // then
        assertTrue(reply.contains("Team"));
    }

    private PlayerData buildPlayer(String slug) {
        PlayerData playerData = new PlayerData();
        playerData.setSlug(slug);
        playerData.setBasketball(new Basketball(new Attributes(1,2)));
        return playerData;
    }
}