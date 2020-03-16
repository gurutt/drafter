package org.gurutt.drafter.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "players")
public class PlayerData {

    public static final String COLLECTION = "players";
    public static final String FOOTBALL = "football";
    public static final String BASKETBALL = "basketball";
    public static final String DOTA = "dota";

    @Id
    private String id;
    @Indexed
    private String slug;
    private String name;
    private Basketball basketball;
    private Football football;
    private Dota dota;
    private String extId;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Football {
        private Attributes attributes;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Basketball {
        private Attributes attributes;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Dota {
        private Attributes attributes;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Attributes {
        public Attributes(double skill, double physics) {
            this.skill = skill;
            this.physics = physics;
        }

        public Attributes(double skill) {
            this.skill = skill;
        }

        // 1 - 20
        private double skill;
        // 1 - 12
        private double physics;

        private List<String> roles;
    }

    public Player toPlayer(String type) {
        Attributes attributes = resolveAttributes(type);
        return new Player(slug, attributes.skill, attributes.physics, name, io.vavr.collection.List.ofAll(attributes.roles));
    }

    private Attributes resolveAttributes(String type) {
        if (FOOTBALL.equalsIgnoreCase(StringUtils.trim(type))) {
            if (this.football == null) throw new IllegalArgumentException("Attributes are not specified for " + this.slug);
            return this.football.attributes;
        } else {
            // TODO change this
            if (this.dota == null) throw new IllegalArgumentException("Attributes are not specified for " + this.slug);
            return this.dota.attributes;
        }
    }


    @Override
    public String toString() {
        return "PlayerData{" +
                "slug='" + slug + '\'' +
                ", football=" + football +
                '}';
    }
}
