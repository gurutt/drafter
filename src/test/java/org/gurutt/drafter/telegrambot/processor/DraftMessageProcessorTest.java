package org.gurutt.drafter.telegrambot.processor;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        Map<String, Object> result = messageProcessor.parseCmd("/draft one, two, three");

        assertEquals(result.get(DraftMessageProcessor.PRM_PLAYERS).get().toString(), "one, two, three");
        assertEquals(Option.none(), result.get(DraftMessageProcessor.PRM_SPORT_TYPE));

        Map<String, Object> result1 = messageProcessor.parseCmd("/draft one, two, three | football");

        assertEquals(result1.get(DraftMessageProcessor.PRM_PLAYERS).get().toString().trim(), "one, two, three");
        assertEquals("football", result1.get(DraftMessageProcessor.PRM_SPORT_TYPE).get().toString().trim());

    }

    @Test
    void parseCmdAttr() {

        Map<String, Object> result1 = messageProcessor.parseCmd("/draft one, two, three | basketball | Skill");

        assertEquals(result1.get(DraftMessageProcessor.PRM_PLAYERS).get().toString().trim(), "one, two, three");
        assertEquals("basketball", result1.get(DraftMessageProcessor.PRM_SPORT_TYPE).get().toString().trim());
        assertEquals("Skill", result1.get(DraftMessageProcessor.PRM_ATTRIBUTE).get().toString().trim());

    }


    @Test
    void match() {
        assertTrue(messageProcessor.match(TestPlayerData.pullDraftUpdate()));
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
        Update update = TestPlayerData.pullDraftUpdate();
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