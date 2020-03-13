package org.gurutt.drafter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Function;

@Data
@AllArgsConstructor
public class DraftContext {

    protected static final int DEFAULT_MAX_DIFF = 5;
    protected static final int DEFAULT_TEAM_COUNT = 2;

    private Function<Player, Double> attr;
    private Integer maxDiff = DEFAULT_MAX_DIFF;
    private Integer teamCount = DEFAULT_TEAM_COUNT;
    private String sportType;

    private DraftContext(Function<Player, Double> attr) {
        this.attr = attr;
    }

    public static DraftContext of(Function<Player, Double> attr) {
        return new DraftContext(attr);
    }

    public static DraftContext of(Function<Player, Double> attr, Integer teamCount, String sportType) {
        if (teamCount == null) {
            return new DraftContext(attr, DEFAULT_MAX_DIFF, DEFAULT_TEAM_COUNT, sportType.trim());
        } else {
            return new DraftContext(attr, DEFAULT_MAX_DIFF, teamCount, sportType.trim());
        }
    }
}
