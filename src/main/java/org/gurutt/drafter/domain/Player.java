package org.gurutt.drafter.domain;

import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {

    public Player(String slug, List<String> roles) {
        this.slug = slug;
        this.roles = roles;
    }

    public Player(String slug, double skill, double physics, String name) {
        this.slug = slug;
        this.skill = skill;
        this.physics = physics;
        this.name = name;
    }

    public Player(String slug, double skill, double physics, String name, List<String> roles) {
        this.slug = slug;
        this.skill = skill;
        this.physics = physics;
        this.name = name;
        this.roles = roles;
    }

    private String slug;
    private double skill;
    private double physics;
    private String name;
    private List<String> roles;
    private String suggestedRole;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Double.compare(player.skill, skill) == 0 &&
                Double.compare(player.physics, physics) == 0 &&
                Objects.equals(slug, player.slug) &&
                Objects.equals(name, player.name) &&
                Objects.equals(roles, player.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug, skill, physics, name, roles);
    }
}
