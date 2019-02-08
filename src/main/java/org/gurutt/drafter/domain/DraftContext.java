package org.gurutt.drafter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Function;

@Data
@AllArgsConstructor
public class DraftContext {

    private Function<Player, Integer> attr;
    private Integer minDiff = 5;

    private DraftContext(Function<Player, Integer> attr) {
        this.attr = attr;
    }

    public static DraftContext of(Function<Player, Integer> attr) {
        return new DraftContext(attr);
    }
}
