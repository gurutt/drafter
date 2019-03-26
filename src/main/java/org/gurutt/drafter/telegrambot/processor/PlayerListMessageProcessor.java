package org.gurutt.drafter.telegrambot.processor;

import io.vavr.collection.List;
import org.gurutt.drafter.domain.Player;
import org.gurutt.drafter.service.PlayerSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.apache.commons.lang3.StringUtils.startsWith;

@Component
public class PlayerListMessageProcessor implements MessageProcessor<String, List<Player>> {

    private static final String PLAYERS_CMD = "/players";
    private final PlayerSelector playerSelector;

    @Autowired
    public PlayerListMessageProcessor(PlayerSelector playerSelector) {
        this.playerSelector = playerSelector;
    }

    @Override
    public String parseCmd(String text) {
        String cmd = text.replaceAll(PLAYERS_CMD, "").trim();
        Assert.state(!cmd.isEmpty(), "Sport type is not specified");
        return cmd;
    }

    @Override
    public List<Player> process(Message message, String params) {
        return playerSelector.listPlayers(params);
    }

    @Override
    public boolean match(Update update) {
        return startsWith(update.getMessage().getText(), PLAYERS_CMD);
    }

    @Override
    public String response(List<Player> players) {
        return String.join("\n",
                players.sortBy(Player::getName).map(player -> String.format("%s - %s (%s)", player.getName(), player.getSkill(), player.getSlug())));
    }
}
