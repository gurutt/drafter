package org.gurutt.drafter.service;

import io.vavr.Tuple;
import io.vavr.Tuple5;
import io.vavr.collection.List;
import org.gurutt.drafter.domain.DraftContext;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.gurutt.drafter.service.TestPlayerData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SlotSwapDrafterTest {

    private SlotSwapDrafter oneByOneDrafter = new SlotSwapDrafter();

    @BeforeAll
    static void init() {
        load();
    }

    @ParameterizedTest(name = "{index} => Data=''{0}''")
    @MethodSource("buildTestCases")
    void decideWithLowestDiffa(Tuple5 data) {
        List<Player> players = players((List<String>) data._4);

        LineUp lineUp = oneByOneDrafter.decide(players, new DraftContext(Player::getSkill, 5, (Integer) data._5, ""));
        assertEquals(data._1, skillDiff(lineUp));
        assertEquals(data._2, lineUp.getTeams().get(0).overallSkill());
        assertEquals(data._3, lineUp.getTeams().get(1).overallSkill());
    }

    private static Stream<Tuple5> buildTestCases() {
        return Stream.of(
                Tuple.of(1.0, 28.0, 27.0, List.of(VALIK, IGOR, NIKITA, ROMA), 2),
                Tuple.of(6.0, 25.0, 19.0, List.of(VALIK, VANYA, REUS, NIKITA), 2),
                Tuple.of(6.0, 25.0, 19.0, List.of(VALIK, VANYA, REUS, NIKITA), 2),
                Tuple.of(1.0, 60.0, 59.0, List.of(VALIK, VANYA, REUS, NIKITA, YURA, ROMA, KOLYA, DIMONR, ROST, IGOR), 2),
                Tuple.of(0.5, 30.5, 31.0, List.of(VALIK, VANYA, REUS, NIKITA, YURA), 2)
        );
    }

    @Test
    void testNTeams() {
        List<Player> players = players(List.of(VALIK, VANYA, REUS, NIKITA, YURA, ROMA, KOLYA, DIMONR, ROST));
        LineUp lineUp = oneByOneDrafter.decide(players, new DraftContext(Player::getSkill, 5, 3, ""));

        assertEquals(1, skillDiff(lineUp));
    }

    private double skillDiff(LineUp lineUp) {
        return Math.abs(lineUp.getTeams().get(0).overallSkill() - lineUp.getTeams().get(lineUp.getTeams().size() - 1).overallSkill());
    }
}
