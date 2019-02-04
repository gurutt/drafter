package org.gurutt.drafter.telegrambot;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.gurutt.drafter.domain.Team;

import static org.gurutt.drafter.service.LineUpEngine.SKILL;
import static org.gurutt.drafter.service.LineUpEngine.STAMINA;

class BotResponse {

    static String lineUp(Map<String, LineUp> lines) {
        LineUp skill = lines.get(SKILL).get();
        StringBuilder builder = new StringBuilder();
        builder.append("*Skill version*\n");
        builder.append(teams(skill.getWest(), skill.getEast(), "skill"));
        builder.append("\n\n");

        LineUp stamina = lines.get(STAMINA).get();
        builder.append("*Stamina version*\n");
        builder.append(teams(stamina.getWest(), stamina.getEast(), "stamina"));

        return builder.toString();
    }

    private static String teams(Team west, Team east, String type) {

        StringBuilder builder = new StringBuilder();
        builder.append(roster(west));
        builder.append(type.equals("skill") ? metadataSkill(west) : metadataStamina(west));
        builder.append("\n");
        builder.append(roster(east));
        builder.append(type.equals("skill") ? metadataSkill(east) : metadataStamina(east));

        return builder.toString();

    }

    private static String roster(Team team) {
        return "*Team:* " + String.join(", ", team.getPlayers().map(Player::getName)) + "\n";
    }

    private static String metadataSkill(Team team) {
        return "*Total Skill:* " + team.overallSkill() + "\n";
    }

    private static String metadataStamina(Team team) {
        return "*Total Stamina:* " + team.overallPhysics() + "\n";
    }
}
