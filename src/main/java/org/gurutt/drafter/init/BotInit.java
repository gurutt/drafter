package org.gurutt.drafter.init;

import lombok.extern.slf4j.Slf4j;
import org.gurutt.drafter.telegrambot.DraftifyBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Order(2)
@Component
@Slf4j
public class BotInit implements ApplicationRunner {

    private final DraftifyBot bot;

    @Autowired
    public BotInit(DraftifyBot bot) {
        this.bot = bot;
    }

    @Override
    public void run(ApplicationArguments args) {

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            LOGGER.error("Issue: ", e);
        }

    }
}
