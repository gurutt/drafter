package org.gurutt.drafter.service;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.gurutt.drafter.domain.DraftContext;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class LineUpEngine {

    public static final String SKILL = "Skill";
    public static final String STAMINA = "Stamina";

    private static final Function<Player, Double> GET_SKILL = Player::getSkill;
    private static final Function<Player, Double> GET_STAMINA = Player::getPhysics;
    private static final Map<String, Function> ATTRIBUTES = HashMap.of(
            SKILL, GET_SKILL,
            STAMINA, GET_STAMINA);

    private final Drafter drafter;

    @Autowired
    public LineUpEngine(Drafter drafter) {
        this.drafter = drafter;
    }

    Map<String, LineUp> decide(List<Player> players, List<String> params, Integer teamCount) {
        if (params.isEmpty()) {
            return HashMap.of(SKILL, drafter.decide(players, DraftContext.of(GET_SKILL, teamCount)));
        }
        return params.map(key -> new Tuple2<>(key,
                drafter.decide(players, DraftContext.of(ATTRIBUTES.get(key).get(), teamCount)))).collect(LinkedHashMap.collector());
    }
}
