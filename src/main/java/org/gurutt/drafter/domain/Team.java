package org.gurutt.drafter.domain;

import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Team {

    private List<Player> players;

    public int overallSkill() {
        return players.map(Player::getSkill).sum().intValue();
    }

    public int overallPhysics() {
        return players.map(Player::getPhysics).sum().intValue();
    }

    @Override
    public String toString() {
        return "Team{" +
                "players=" + String.join(",", players.map(Player::getName)) +
                ", overallSkill=" + overallSkill() +
                ", overallPhysics=" + overallPhysics() +
                '}';
    }
}
