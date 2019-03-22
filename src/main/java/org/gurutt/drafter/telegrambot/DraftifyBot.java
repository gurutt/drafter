package org.gurutt.drafter.telegrambot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.gurutt.drafter.telegrambot.processor.MessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWN;

@Component
@Slf4j
public class DraftifyBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String token;

    //Spring can't autowire vavr list so...
    private final java.util.List<MessageProcessor> processors;

    @Autowired
    public DraftifyBot(java.util.List<MessageProcessor> processors) {
        this.processors = processors;
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        Long chat_id = null;
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                chat_id = update.getMessage().getChatId();

                String replyMsg = processors.stream()
                        .filter(m -> m.match(update))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Couldn't find matched processor"))
                        .handle(update.getMessage());

                reply(chat_id, replyMsg);
            }
        } catch (Exception e) {
            LOGGER.error("Issue: ", e);
            if (chat_id != null) {
                reply(chat_id, e.getMessage());
            }
        }
    }

    private void reply(Long chat_id, String msg) throws TelegramApiException {
        SendMessage reply = new SendMessage()
                .setChatId(chat_id)
                .setParseMode(MARKDOWN)
                .setText(msg);
        send(reply);
    }

    void send(SendMessage error) throws TelegramApiException {
        execute(error);
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
