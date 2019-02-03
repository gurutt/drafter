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


    public Object build(List<String> participants) {
        return null;
    }

    public LineUp pick(List<Player> players) {
        LineUp lineUp = new LineUp();

        // temporary
        Function<Player, Integer> getSkill = Player::getSkill;
        players = players.sortBy(getSkill).reverse();


        List<Player> west = players.zipWithIndex()
                .filter(t -> t._2 % 2 == 0)
                .map(t -> t._1);

        List<Player> east = players.drop(1).zipWithIndex()
                .filter(t -> t._2 % 2 == 0)
                .map(t -> t._1);


        Player worth = players.get(players.size() - 1);

        Tuple2<Integer, Integer> skills = totalValue(west, east, getSkill);
        int i = 0;
        while (Math.abs(skills._1 - skills._2) > worth.getSkill()) {
            int idx = west.size() - 1;
            if (i == idx + 1) {
                break;
            }
            int index = idx - i;
            Player p1 = west.get(index);
            Player p2 = east.get(index);
            west = west.replace(p1, p2);
            east = east.replace(p2, p1);
            Tuple2<Integer, Integer> newSkills = totalValue(west, east, getSkill);
            if (Math.abs(skills._1 - skills._2) < Math.abs(newSkills._1 - newSkills._2)) {
                break;
            }
            skills = newSkills;
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
