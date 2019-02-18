package org.gurutt.drafter.telegrambot.processor;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.gurutt.drafter.domain.Team;
import org.gurutt.drafter.service.PlayerSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.BiFunction;

import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.StringUtils.wrap;
import static org.gurutt.drafter.service.LineUpEngine.SKILL;
import static org.gurutt.drafter.service.LineUpEngine.STAMINA;

@Component
public class DraftMessageProcessor implements MessageProcessor<Tuple2<List<String>, String>, Map<String, LineUp>> {

    private static final String BOLD = "*";

    private static final BiFunction<Team, String, String> fSkill =
            (team, type) -> String.format(wrap("Total %s: ", BOLD), type) + team.overallSkill() + "\n";

    private static final BiFunction<Team, String, String> fStamina =
            (team, type) -> String.format(wrap("Total %s: ", BOLD), type) + team.overallPhysics() + "\n";

    private static final Map<String, BiFunction> DETAILS = HashMap.of(SKILL, fSkill, STAMINA, fStamina);

    private static final String DRAFT_CMD = "/draft";

    private final PlayerSelector playerSelector;

    @Autowired
    public DraftMessageProcessor(PlayerSelector playerSelector) {
        this.playerSelector = playerSelector;
    }

    @Override
    public Tuple2<List<String>, String> parseCmd(String text) {
        String[] params = text.replaceAll(DRAFT_CMD, "").trim().split("\\|");
        List<String> participants = List.of(params[0].trim().split("\\s*,\\s*"));

        return Tuple.of(participants, params.length > 1 ? params[1].trim() : null);
    }

    @Override
    public Map<String, LineUp> process(Message message, Tuple2<List<String>, String> params) {
        return playerSelector.select(params._1, params._2);
    }

    @Override
    public boolean match(Update update) {
        return startsWith(update.getMessage().getText(), DRAFT_CMD);
    }

    @Override
    public String response(Map<String, LineUp> teams) {
        return success(teams);
    }

    private String success(Map<String, LineUp> lines) {
        StringBuilder builder = new StringBuilder();
        lines.forEach(l -> {
            builder.append(String.format(wrap("%s version", BOLD) + "\n", l._1));
            builder.append(teams(List.of(l._2.getWest(), l._2.getEast()), l._1));
            builder.append("\n\n");
        });
        return builder.toString();
    }

    private String teams(List<Team> teams, String type) {

        StringBuilder builder = new StringBuilder();
        teams.forEach(team -> {
            builder.append(roster(team));
            builder.append(DETAILS.get(type).get().apply(team, type));
            builder.append("\n");
        });

        return builder.toString();
    }

    private String roster(Team team) {
        return wrap("Team: ", BOLD) + String.join(", ", team.getPlayers().map(Player::getName)) + "\n";
    }
}
