package org.gurutt.drafter.telegrambot;

import io.vavr.collection.List;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.gurutt.drafter.domain.Team;

public class BotResponse {

    public static String lineUp(List<LineUp> lines) {
        LineUp skill = lines.get(0);
        StringBuilder builder = new StringBuilder();
        builder.append("*Skill version*\n");
        builder.append(teams(skill.getWest(), skill.getEast(), "skill"));
        builder.append("\n\n");

        LineUp stamina = lines.get(1);
        builder.append("*Stamina version*\n");
        builder.append(teams(stamina.getWest(), stamina.getEast(), "stamina"));

        return builder.toString();
    }

    public static String teams(Team west, Team east, String type) {

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
