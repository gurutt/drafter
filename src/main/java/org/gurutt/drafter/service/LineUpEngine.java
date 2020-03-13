package org.gurutt.drafter.service;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.gurutt.drafter.domain.DraftContext;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

    @Resource(name = "drafters")
    private java.util.Map<String, Drafter> drafters;

    public LineUpEngine(java.util.Map<String, Drafter> drafters) {
        this.drafters = drafters;
    }

    Map<String, LineUp> decide(List<Player> players, String sportType, Integer teamCount) {

        HashMap<String, Drafter> algos = HashMap.ofAll(drafters);
        return algos.map(d -> new Tuple2<>(d._1, d._2.decide(players, DraftContext.of(GET_SKILL, teamCount, sportType))))
                .collect(LinkedHashMap.collector());
    }
}
