package org.gurutt.drafter.domain;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Slf4j
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

    public Boolean validateRoles(String type) {

        if (type.equals(PlayerData.DOTA)) {
            java.util.List<String> dotaRoles = initDotaRoles();
            List<Player> players = this.players;
            java.util.Set<String> selectedRoles = new HashSet<>();
            java.util.Set<Player> selectedPlayers = new HashSet<>();

            List<Player> singleRolePlayer = players.filter(p -> p.getRoles().size() == 1);
            for (Player player : singleRolePlayer) {
                String role = player.getRoles().get(0);
                if (selectedRoles.contains(role)) {
                    return false;
                }
                selectedRoles.add(role);
                selectedPlayers.add(player);
                player.setSuggestedRole(role);
            }

            dotaRoles.removeAll(selectedRoles);
            players = players.removeAll(selectedPlayers);

            Map<String, Long> frequency = players.flatMap(Player::getRoles)
                    .removeAll(selectedRoles)
                    .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

            for (Map.Entry<String, Long> entry : frequency.entrySet()) {
                String role = entry.getKey();
                Long v = entry.getValue();
                if (v.equals(1L)) {
                    Option<Player> playerOption = players.find(player -> player.getRoles().contains(role));
                    Player player = playerOption.get();
                    if (!playerOption.isEmpty() && !selectedPlayers.contains(player)) {
                        selectedPlayers.add(player);
                        selectedRoles.add(role);
                        player.setSuggestedRole(role);
                    }
                }
            }

            dotaRoles.removeAll(selectedRoles);
            players = players.removeAll(selectedPlayers);

            for (String r : dotaRoles) {
                Option<Player> player = players.find(p -> p.getRoles().contains(r) && !selectedPlayers.contains(p) && !selectedRoles.contains(r))
                        .orElse(Option.none());
                if (player.isEmpty()) {
                    //LOGGER.warn("No player found for role" + r);
                    System.out.println("No player found for role" + r);
                } else {
                    selectedRoles.add(r);
                    selectedPlayers.add(player.get());
                    player.get().setSuggestedRole(r);
                }
            }
            if (selectedPlayers.size() == 5) {
                System.out.println("Found the team " + this);
                return true;
            }
        }
        return false;
    }

    private java.util.List<String> initDotaRoles() {
        java.util.List<String> dotaRoles = new ArrayList<>();
        dotaRoles.add("1");
        dotaRoles.add("2");
        dotaRoles.add("3");
        dotaRoles.add("4");
        dotaRoles.add("5");
        return dotaRoles;
    }

    @Override
    public String toString() {
        ;
        return "Team{" +
                //"skills=" + String.join(",", players.map(Player::getSkill).toString()) +
                "players=" + String.join(",", players.map(p -> p.getSlug() + String.join("|", p.getRoles()))) +
                //", overallSkill=" + overallSkill() +
                //", overallPhysics=" + overallPhysics() +
                '}';
    }
}
