package org.gurutt.drafter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "players")
public class Player {

    public static final String COLLECTION = "players";

    public Player(String slug, int skill, int physics) {
        this.skill = skill;
        this.physics = physics;
        this.slug = slug;
    }

    @Id
    private String id;
    // 1 - 20
    private int skill;
    // 1 - 12
    private int physics;
    private String name;
    @Indexed
    private String slug;
}
