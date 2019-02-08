package org.gurutt.drafter.service;

import io.vavr.collection.List;
import org.gurutt.drafter.domain.DraftContext;
import org.gurutt.drafter.domain.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.gurutt.drafter.service.TestPlayerData.NIKITA;
import static org.gurutt.drafter.service.TestPlayerData.ROMA;
import static org.gurutt.drafter.service.TestPlayerData.VANYA;
import static org.gurutt.drafter.service.TestPlayerData.YURA;
import static org.gurutt.drafter.service.TestPlayerData.load;

class OneByOneDrafterTest {

    private OneByOneDrafter oneByOneDrafter = new OneByOneDrafter();

    @BeforeAll
    static void init() {
        load();
    }

    @Test
    void decide() {

        // given
        List<Player> players = TestPlayerData.players(YURA, ROMA, VANYA, NIKITA);

        //
        oneByOneDrafter.decide(players, DraftContext.of(Player::getSkill));
    }
}