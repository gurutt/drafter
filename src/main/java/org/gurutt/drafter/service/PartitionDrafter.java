package org.gurutt.drafter.service;


import org.gurutt.drafter.domain.DraftContext;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.gurutt.drafter.domain.Team;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.primitives.Ints.asList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;


@Component
public class PartitionDrafter implements Drafter {

    private class Result {
        int teams;
        int players;
        List<Integer> weights = new ArrayList<>();
        List<List<Integer>> values = new ArrayList<>();

        public Result(int teams, int players, Integer weight, List<Integer> values) {
            this.teams = teams;
            this.players = players;
            this.weights.add(weight);
            this.values.add(values);
        }

        public int min() {
            return weights.stream().reduce(Math::min).orElseThrow(RuntimeException::new);
        }

        public int max() {
            return weights.stream().reduce(Math::max).orElseThrow(RuntimeException::new);
        }

        public int diff() {
            if (values.size() != teams) {
                return Integer.MAX_VALUE;
            }
            for (List<Integer> value : values) {
                if (value.size() != players) {
                    return Integer.MAX_VALUE;
                }
            }
            if (weights.size() != teams) {
                return Integer.MAX_VALUE;
            }
            return Math.abs(max() - min());
        }

        public List<Integer> bucket() {
            List<Integer> bucket = new ArrayList<>();
            for (List<Integer> value : values) {
                bucket.addAll(value);
            }
            return bucket;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Weights: ");
            builder.append(weights.stream().map(String::valueOf).collect(Collectors.joining(", ")));
            builder.append("\nValues:\n");
            for (List<Integer> value : values) {
                builder.append(value.stream().map(String::valueOf).collect(Collectors.joining(", ")));
                builder.append("\n");
            }
            return builder.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Result result = (Result) o;

            boolean eq = teams == result.teams;

            if (!eq) {
                return false;
            }

            List<Integer> copy = new ArrayList<>(result.weights);

            for (int i = 0; i < weights.size(); i++) {
                copy.remove(weights.get(i));
            }

            if (!copy.isEmpty()) {
                return false;
            }

            int ok = 0;

            for (List<Integer> first : values) {
                for (List<Integer> second : result.values) {
                    if (second.equals(first)) {
                        ok++;
                    }
                }
            }
            return ok == teams;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

    private List<List<List<Integer>>> generate(Map<Integer, Long> freq, int tolerance) {
        int min = freq.keySet().stream().reduce(Math::min).orElseThrow(RuntimeException::new);
        List<List<List<Integer>>> sums = new ArrayList<>(tolerance);
        for (int i = 0; i < tolerance; i++) {
            sums.add(i, new ArrayList<>());
        }

        for (Integer n : freq.keySet()) {
            if (freq.get(n) == 0) {
                continue;
            }
            if (sums.get(n).isEmpty()) {
                List<List<Integer>> lst = new ArrayList<>();
                lst.add(new ArrayList<>(asList(n)));
                sums.set(n, lst);
            } else {
                sums.get(n).add(new ArrayList<>(asList(n)));
            }

            for (int i = min; i < tolerance - n; i++) {
                if (!sums.get(i).isEmpty()) {
                    List<List<Integer>> lists = sums.get(i);
                    for (List<Integer> numbers : lists) {
                        ArrayList<Integer> copy = new ArrayList<>(numbers);
                        copy.add(n);
                        if (copy.stream().filter(item -> item.equals(n)).count() <= freq.get(n)) {
                            sums.get(i + n).add(copy);
                        }
                    }
                }
            }
        }
        return sums;
    }

    private Map<Integer, Long> book(Map<Integer, Long> freq, List<Integer> bucket) {
        Map<Integer, Long> left = new HashMap<>(freq);
        for (Integer item : bucket) {
            left.put(item, left.get(item) - 1);
        }
        return left;
    }

    public List<Result> doDraft(double[] input, int teams) {

        int[] players = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            players[i] = (int) (input[i] * 100);
        }

        int sum = Arrays.stream(players).sum();
        int avg = sum / players.length;
        int target = sum / teams;

        int min = target - avg / 10;
        int max = target + avg / 10;

        Map<Integer, Long> freq = Arrays.stream(players).boxed()
                .collect(groupingBy(identity(), counting()));

        List<Result> results = new ArrayList<>();
        {
            List<List<List<Integer>>> sums = generate(freq, max);
            for (int j = min; j < max; j++) {
                List<List<Integer>> buckets = sums.get(j);
                for (List<Integer> bucket : buckets) {
                    results.add(new Result(teams, players.length / teams, j, bucket));
                }
            }
        }

        for (int i = 0; i < teams - 1; i++) {
            List<Result> overflow = new ArrayList<>();
            for (Result result : results) {
                List<List<List<Integer>>> sums = generate(book(freq, result.bucket()), max);
                for (int j = min; j < max; j++) {
                    List<List<Integer>> buckets = sums.get(j);
                    if (!buckets.isEmpty()) {
                        int k = 0;
                        if (result.weights.size() <= i + 1) {
                            result.weights.add(j);
                            result.values.add(buckets.get(0));
                            k++;
                        }
                        for (; k < buckets.size(); k++) {
                            Result more = new Result(teams, players.length / teams, result.weights.get(0), result.values.get(0));
                            more.weights.add(j);
                            more.values.add(buckets.get(k));
                            overflow.add(more);
                        }
                    }
                }
            }
            results.addAll(overflow);
        }

        int diff = results.stream()
                .map(Result::diff)
                .reduce(Math::min)
                .orElseThrow(RuntimeException::new);

        if (diff == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Wrong members");
        } else {
            return results.stream()
                    .filter(result -> result.diff() == diff)
                    .distinct()
                    .collect(Collectors.toList());
        }
    }

    @Override
    public LineUp decide(io.vavr.collection.List<Player> players, DraftContext draftContext) {

        Function<Player, Double> attr0 = draftContext.getAttr();
        Function<Player, Double> attr = attr0.andThen(d -> (double) Math.round(d * 100));
        double[] playersArray = players.map(attr).toJavaList().stream().mapToDouble(e -> e / 100).toArray();

        List<Result> results = doDraft(playersArray, draftContext.getTeamCount());

        List<LineUp> lineUps = new ArrayList<>();
        for (Result result : results) {
            io.vavr.collection.List<Player> list = players;
            LineUp lineUp = new LineUp();
            List<Team> teams = new ArrayList<>();
            for (List<Integer> teamValues : result.values) {
                List<Player> playerResult = new ArrayList<>();

                for (Integer playerValue : teamValues) {
                    Predicate<Player> reverseMapping = p -> attr.apply(p).intValue() == playerValue.intValue();
                    Player player = list.find(reverseMapping).get();
                    list = list.remove(player);
                    playerResult.add(player);
                }
                Team team = new Team(io.vavr.collection.List.ofAll(playerResult));
                teams.add(team);
            }
            lineUp.setTeams(io.vavr.collection.List.ofAll(teams));
            lineUps.add(lineUp);
        }

        return lineUps.get(0);
    }
}
