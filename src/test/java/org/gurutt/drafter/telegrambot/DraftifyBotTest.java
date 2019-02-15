package org.gurutt.drafter.telegrambot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gurutt.drafter.config.PlayerSelectorConfiguration;
import org.gurutt.drafter.domain.Player;
import org.gurutt.drafter.domain.PlayerData;
import org.gurutt.drafter.service.PlayerSelector;
import org.gurutt.drafter.service.TestPlayerData;
import org.gurutt.drafter.telegrambot.processor.DraftMessageProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PlayerSelectorConfiguration.class})
class DraftifyBotTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Autowired
    private PlayerSelector playerSelector;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Spy
    private DraftifyBot draftifyBot = new DraftifyBot(Arrays.asList(new DraftMessageProcessor(playerSelector)));

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(PlayerData.COLLECTION);
    }

    @Test
    void onUpdateReceived() throws Exception {

        doNothing().when(draftifyBot).send(any(SendMessage.class));

        Update update = TestPlayerData.pullUpdate();

        draftifyBot.onUpdateReceived(update);

    }
}