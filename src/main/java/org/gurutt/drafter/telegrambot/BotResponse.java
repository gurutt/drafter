package org.gurutt.drafter.telegrambot;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.gurutt.drafter.domain.Team;

import java.util.function.BiFunction;

import static org.apache.commons.lang3.StringUtils.wrap;
import static org.gurutt.drafter.service.LineUpEngine.SKILL;
import static org.gurutt.drafter.service.LineUpEngine.STAMINA;


class BotResponse {

    private static final String BOLD = "*";

    private static final BiFunction<Team, String, String> fSkill =
            (team, type) -> String.format(wrap("Total %s: ", BOLD), type) + team.overallSkill() + "\n";

    private static final BiFunction<Team, String, String> fStamina =
            (team, type) -> String.format(wrap("Total %s: ", BOLD), type) + team.overallPhysics() + "\n";

    private static final Map<String, BiFunction> DETAILS = HashMap.of(SKILL, fSkill, STAMINA, fStamina);

    static String success(Map<String, LineUp> lines) {
        StringBuilder builder = new StringBuilder();
        lines.forEach(l -> {
            builder.append(String.format(wrap("%s version", BOLD) + "\n", l._1));
            builder.append(teams(List.of(l._2.getWest(), l._2.getEast()), l._1));
            builder.append("\n\n");
        });
        return builder.toString();
    }

    private static String teams(List<Team> teams, String type) {

        StringBuilder builder = new StringBuilder();
        teams.forEach(team -> {
            builder.append(roster(team));
            builder.append(DETAILS.get(type).get().apply(team, type));
            builder.append("\n");
        });

        return builder.toString();
    }

    private static String roster(Team team) {
        return wrap("Team: ", BOLD) + String.join(", ", team.getPlayers().map(Player::getName)) + "\n";
    }
}
