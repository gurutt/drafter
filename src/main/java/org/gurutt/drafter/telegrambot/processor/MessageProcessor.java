package org.gurutt.drafter.telegrambot.processor;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessageProcessor<P, T> {

    P parseCmd(String text);

    T process(Message message, P params);

    boolean match(Update update);

    String response(T t);

    default String handle(Message message) {
        P params = parseCmd(message.getText());
        T processed = process(message, params);
        return response(processed);
    }


}
