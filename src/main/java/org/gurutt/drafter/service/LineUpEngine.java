package org.gurutt.drafter.service;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.gurutt.drafter.domain.DraftContext;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LineUpEngine {

    public static final String SKILL = "Skill";
    public static final String STAMINA = "Stamina";

    private final Drafter drafter;

    @Autowired
    public LineUpEngine(Drafter drafter) {
        this.drafter = drafter;
    }

    Map<String, LineUp> decide(List<Player> players) {
        return HashMap.of(SKILL, drafter.decide(players, DraftContext.of(Player::getSkill)),
                STAMINA, drafter.decide(players, DraftContext.of(Player::getPhysics)));
    }
}
