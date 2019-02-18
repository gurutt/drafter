package org.gurutt.drafter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    
    private String slug;
    private int skill;
    private int physics;
    private String name;
}
