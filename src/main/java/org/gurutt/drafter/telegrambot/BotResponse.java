package org.gurutt.drafter.telegrambot;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.gurutt.drafter.domain.Team;

import java.util.function.Function;

import static org.gurutt.drafter.service.LineUpEngine.SKILL;
import static org.gurutt.drafter.service.LineUpEngine.STAMINA;


class BotResponse {

    private static final Function<Team, String> fSkill = t -> "*Total Skill:* " + t.overallSkill() + "\n";
    private static final Function<Team, String> fStamina = t -> "*Total Stamina:* " + t.overallPhysics() + "\n";

    private static Map<String, Function> DETAILS = HashMap.of(SKILL, fSkill, STAMINA, fStamina);

    static String lineUp(Map<String, LineUp> lines) {
        StringBuilder builder = new StringBuilder();
        lines.forEach(l -> {
            builder.append(String.format("*%s version*\n", l._1));
            builder.append(teams(List.of(l._2.getWest(), l._2.getEast()), l._1));
            builder.append("\n\n");
        });
        return builder.toString();
    }

    private static String teams(List<Team> teams, String type) {

        StringBuilder builder = new StringBuilder();
        teams.forEach(team -> {
            builder.append(roster(team));
            builder.append(DETAILS.get(type).get().apply(team));
            builder.append("\n");
        });

        return builder.toString();

    }

    private static String roster(Team team) {
        return "*Team:* " + String.join(", ", team.getPlayers().map(Player::getName)) + "\n";
    }
}
