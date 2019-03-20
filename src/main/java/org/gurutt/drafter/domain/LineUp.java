package org.gurutt.drafter.domain;

import io.vavr.collection.List;
import lombok.Data;


@Data
public class LineUp {
    private List<Team> teams;
}
