package org.gurutt.drafter.service;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.gurutt.drafter.domain.Team;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class LineUpEngine {

    public List<LineUp> decide(List<Player> players) {

        return List.of(buildLineUp(players, Player::getSkill), buildLineUp(players, Player::getPhysics));
    }

    private LineUp buildLineUp(List<Player> players, Function<Player, Integer> attr) {
        LineUp lineUp = new LineUp();

        players = players.sortBy(attr).reverse();

        Player worth = players.get(players.size() - 1);

        List<Player> west = players.zipWithIndex()
                .filter(t -> t._2 % 2 == 0)
                .map(t -> t._1);

        List<Player> east = players.drop(1).zipWithIndex()
                .filter(t -> t._2 % 2 == 0)
                .map(t -> t._1);

        Tuple2<Integer, Integer> total = totalValue(west, east, attr);
        int i = 0;
        while (Math.abs(total._1 - total._2) > worth.getSkill()) {
            int idx = west.size() - 1;
            if (i == idx + 1) {
                break;
            }
            int index = idx - i;
            Player p1 = west.get(index);
            Player p2 = east.get(index);
            west = west.replace(p1, p2);
            east = east.replace(p2, p1);
            Tuple2<Integer, Integer> newAttr = totalValue(west, east, attr);
            if (Math.abs(total._1 - total._2) < Math.abs(newAttr._1 - newAttr._2)) {
                break;
            }
            total = newAttr;
            i++;
        }

        lineUp.setWest(new Team(west));
        lineUp.setEast(new Team(east));
        return lineUp;
    }

    private Tuple2<Integer, Integer> totalValue(List<Player> west, List<Player> east, Function<Player, Integer> criterion) {

        int westSkill = west.map(criterion).sum().intValue();
        int eastSkill = east.map(criterion).sum().intValue();

        return new Tuple2<>(westSkill, eastSkill);
    }
}
