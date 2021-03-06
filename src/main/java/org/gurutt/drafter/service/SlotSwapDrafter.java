package org.gurutt.drafter.service;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.gurutt.drafter.domain.DraftContext;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.gurutt.drafter.domain.Team;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.function.Function;

@Component
public class SlotSwapDrafter implements Drafter {

    @Override
    public LineUp decide(List<Player> players, DraftContext draftContext) {
        int teamCount = draftContext.getTeamCount();
        LineUp lineUp = new LineUp();
        Function<Player, Double> attr = draftContext.getAttr();

        List<Player> roster = players.sortBy(attr).reverse();

        if (roster.length() % 2 != 0 && teamCount == 2) {
            double apply = attr.apply(roster.last());
            roster = roster.append(new Player("dummy", apply/2, apply/2, "-", List.empty()));
        }

        List<Player> finalRoster = roster;
        Map<Integer, Team> teams = List.range(0, teamCount)
                .toLinkedMap(i -> Tuple.of(i, new Team(n(finalRoster.drop(i), teamCount))));

        List<Team> extTeams = getTeams(teams);
        double max = getMax(attr, extTeams);
        double min = getMin(attr, extTeams);

        int slot = 0;
        int initialDiff = 1;

        List<Team> result = extTeams;

        while (Math.abs(max - min) > initialDiff && initialDiff <= draftContext.getMaxDiff()) {
            int idx = teams.get(0).get().getPlayers().size() - 1;
            if (slot == idx + 1) {
                initialDiff++;
                slot = 0;
            }
            int index = idx - slot;
            Tuple2<Integer, Team> highTeam = teams.maxBy(Comparator.comparing(entry -> entry._2.attrValue(attr))).get();
            Tuple2<Integer, Team> lowTeam = teams.minBy(Comparator.comparing(entry -> entry._2.attrValue(attr))).get();
            Player p1 = highTeam._2.getPlayers().get(index);
            Player p2 = lowTeam._2.getPlayers().get(index);
            teams = swapPlayers(teams, highTeam, lowTeam, p2, p1);

            List<Team> tempTeams = getTeams(teams);
            double newMax = getMax(attr, tempTeams);
            double newMin = getMin(attr, tempTeams);

            if (Math.abs(max - min) < Math.abs(newMax - newMin)) {
                teams = swapPlayers(teams, highTeam, lowTeam, p1, p2);
                initialDiff++;
            }

            max = newMax;
            min = newMin;
            slot++;
            if (Math.abs(max - min) < Math.abs(getMax(attr, result) - getMin(attr, result))) {
                result = getTeams(teams);
            }
        }

        lineUp.setTeams(result);
        return lineUp;
    }

    private List<Team> getTeams(Map<Integer, Team> teams) {
        return teams.map(r -> r._2).toList();
    }

    private Double getMin(Function<Player, Double> attr, List<Team> teams) {
        return teams.map(t -> t.attrValue(attr)).min().get();
    }

    private Double getMax(Function<Player, Double> attr, List<Team> teams) {
        return teams.map(t -> t.attrValue(attr)).max().get();
    }

    private Map<Integer, Team> swapPlayers(Map<Integer, Team> teams, Tuple2<Integer, Team> w,
                                           Tuple2<Integer, Team> e, Player p1, Player p2) {
        teams = teams.put(w._1, new Team(w._2.getPlayers().replace(p2, p1)));
        teams = teams.put(e._1, new Team(e._2.getPlayers().replace(p1, p2)));
        return teams;
    }

    private List<Player> n(List<Player> players, int teamCount) {
        return players.zipWithIndex()
                .filter(t -> t._2 % teamCount == 0)
                .map(t -> t._1);
    }

}
