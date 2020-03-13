package org.gurutt.drafter.domain;

import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TeamTest {

    @Test
    void validateRoles() {
        Player p1 = new Player("vanya", List.of("1","2"));
        Player p2 = new Player("danil", List.of("1"));
        Player p3 = new Player("malina", List.of("3"));
        Player p4 = new Player("pudge", List.of("2","4"));
        Player p5 = new Player("vova-g", List.of("4","5"));
        Player p6 = new Player("nikita", List.of("1","2"));
        Player p7 = new Player("artem", List.of("2","3", "4"));
        // Ваня - 2, Данил - 1, Саша - 3, Бершов - 2, Вова - 5
        Team team = new Team(List.of(p1, p2, p3, p4, p5));
        System.out.println(team.validateRoles("dota"));
        print(team);

        //Саша, Ваня, Никита, Данил, Артем false
        Team team1 = new Team(List.of(p1, p2, p3, p6, p7));
        //System.out.println(team1.validateRoles("dota"));

    }

    void print(Team team) {
        System.out.println(String.join(", ", team.getPlayers()
                .map(player -> player.getSlug() + " - " + player.getSuggestedRole())));
    }
}
