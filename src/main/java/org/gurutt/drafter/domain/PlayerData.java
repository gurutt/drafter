package org.gurutt.drafter.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "players")
public class PlayerData {

    public static final String COLLECTION = "players";
    public static final String FOOTBALL = "football";
    public static final String BASKETBALL = "basketball";

    @Id
    private String id;
    @Indexed
    private String slug;
    private String name;
    private Basketball basketball;
    private Football football;

    @Data
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
    public static class Attributes {
        // 1 - 20
        private int skill;
        // 1 - 12
        private int physics;
    }

    public Player toPlayer(String type) {
        Attributes attributes = resolveAttributes(type);
        return new Player(slug, attributes.skill, attributes.physics, name);
    }

    private Attributes resolveAttributes(String type) {
        if (FOOTBALL.equalsIgnoreCase(StringUtils.trim(type))) {
            if (this.football == null) throw new IllegalArgumentException("Attributes are not specified for " + type);
            return this.football.attributes;
        } else {
            if (this.basketball == null) throw new IllegalArgumentException("Attributes are not specified for " + type);
            return this.basketball.attributes;
        }
    }

}
