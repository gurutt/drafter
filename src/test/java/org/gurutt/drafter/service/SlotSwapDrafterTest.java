package org.gurutt.drafter.service;

import io.vavr.Tuple;
import io.vavr.Tuple4;
import io.vavr.collection.List;
import org.gurutt.drafter.domain.DraftContext;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.junit.jupiter.api.BeforeAll;
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
    void decideWithLowestDiffa(Tuple4 data) {
        List<Player> players = players((List<String>) data._4);

        LineUp lineUp = oneByOneDrafter.decide(players, DraftContext.of(Player::getSkill));
        assertEquals(data._1, skillDiff(lineUp));
        assertEquals(data._2, lineUp.getWest().overallSkill());
        assertEquals(data._3, lineUp.getEast().overallSkill());
    }

    private static Stream<Tuple4> buildTestCases() {
        return Stream.of(
                Tuple.of(1, 28, 27, List.of(VALIK, IGOR, NIKITA, ROMA)),
                Tuple.of(6, 25, 19, List.of(VALIK, VANYA, REUS, NIKITA)),
                Tuple.of(6, 25, 19, List.of(VALIK, VANYA, REUS, NIKITA)),
                Tuple.of(1, 60, 59, List.of(VALIK, VANYA, REUS, NIKITA, YURA, ROMA, KOLYA, DIMONR, ROST, IGOR))
        );
    }

    private int skillDiff(LineUp decide) {
        return decide.getWest().overallSkill() - decide.getEast().overallSkill();
    }
}