package org.gurutt.drafter.domain;

import io.vavr.collection.List;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class LineUp {
    private List<Team> teams;
}
