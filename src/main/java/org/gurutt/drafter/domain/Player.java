package org.gurutt.drafter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {

    public Player(String name, int skill, int physics) {
        this.skill = skill;
        this.physics = physics;
        this.name = name;
    }

    private long id;
    // 1 - 20
    private int skill;
    // 1 - 12
    private int physics;
    private String name;
}
