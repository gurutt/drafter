package org.gurutt.drafter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    
    private String slug;
    private double skill;
    private double physics;
    private String name;
}
