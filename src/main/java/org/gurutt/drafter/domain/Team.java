package org.gurutt.drafter.domain;

import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Function;

@Data
@AllArgsConstructor
public class Team {

    private List<Player> players;

    public double overallSkill() {
        return players.map(Player::getSkill).sum().doubleValue();
    }

    public double overallPhysics() {
        return players.map(Player::getPhysics).sum().doubleValue();
    }

    public Double attrValue(Function<Player, Double> criterion) {
        return players.map(criterion).sum().doubleValue();
    }

    @Override
    public String toString() {
        return "Team{" +
                "skills=" + String.join(",", players.map(Player::getSkill).toString()) +
                "players=" + String.join(",", players.map(Player::getSlug)) +
                ", overallSkill=" + overallSkill() +
                ", overallPhysics=" + overallPhysics() +
                '}';
    }
}
