package org.gurutt.drafter.telegrambot;

import io.vavr.collection.List;
import lombok.extern.slf4j.Slf4j;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.service.PlayerSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;

@Component
@Slf4j
public class DraftifyBot extends TelegramLongPollingBot {

    protected static final String CMD = "/draft";
    @Value("${telegram.bot.token}")
    private String token;

    private final PlayerSelector playerSelector;

    @Autowired
    public DraftifyBot(PlayerSelector playerSelector) {
        this.playerSelector = playerSelector;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().contains(CMD)) {
            Message message = update.getMessage();
            long chat_id = update.getMessage().getChatId();

            List<LineUp> select = List.of();
            try {
                java.util.List<String> participants = Arrays.asList(message.getText()
                        .replaceAll(CMD,"")
                        .trim()
                        .split("\\s*,\\s*"));
                select = playerSelector.select(participants);
            } catch (Exception e) {
                LOGGER.error("Issue: ", e);
                SendMessage error = new SendMessage()
                        .setChatId(chat_id)
                        .setParseMode("markdown")
                        .setText("_Unable to parse incoming msg, use comma separated list with known players._");
                try {
                    execute(error);
                } catch (TelegramApiException e1) {
                    LOGGER.error("Issue: ", e);
                }
                return;
            }

            SendMessage send = new SendMessage()
                    .setChatId(chat_id)
                    .setParseMode("markdown")
                    .setText(BotResponse.lineUp(select));
            try {
                execute(send);
            } catch (TelegramApiException e) {
                LOGGER.error("Issue: ", e);

            }

        }

    }

    @Override
    public String getBotUsername() {
        return "DraftifyBot";
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
