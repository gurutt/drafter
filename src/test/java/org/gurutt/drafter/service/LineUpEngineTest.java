package org.gurutt.drafter.service;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.gurutt.drafter.service.TestPlayerData.IGOR;
import static org.gurutt.drafter.service.TestPlayerData.NIKITA;
import static org.gurutt.drafter.service.TestPlayerData.ROMA;
import static org.gurutt.drafter.service.TestPlayerData.YURA;
import static org.gurutt.drafter.service.TestPlayerData.load;
import static org.gurutt.drafter.service.TestPlayerData.players;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class LineUpEngineTest {

    private static final String algorithm = "slot-swap";
    private Map<String, Drafter> drafters = HashMap.of(algorithm, new SlotSwapDrafter());
    private LineUpEngine lineUpEngine = new LineUpEngine(drafters.toJavaMap());;

    @BeforeAll
    static void init() {
        load();
    }

    @Test
    void testDefaultAttr() {

        // given
        List<Player> players = players(YURA, ROMA, IGOR, NIKITA);

        // when
        Map<String, LineUp> rosters = lineUpEngine.decide(players, "", 2);

        // then
        assertTrue(rosters.containsKey(algorithm));
    }

    @Test
    void testMultipleAttrs() {

        // given
        List<Player> players = players(YURA, ROMA, IGOR, NIKITA);

        // when
        Map<String, LineUp> rosters = lineUpEngine.decide(players, "", 2);

        // then
        assertTrue(rosters.containsKey(algorithm));
        assertEquals(1, rosters.size());
    }

    @Test
    void testBigGame() {

        Player p1 = new Player("yura", 14, 10, null);
        Player p2 = new Player("roshin", 11, 9,null);
        Player p3 = new Player("igor", 16, 9, null);
        Player p4 = new Player("kolya", 15, 8, null);
        Player p5 = new Player("reus", 9, 6, null);
        Player p6 = new Player("nikita", 9, 8,null);
        Player p7 = new Player("roma", 11, 7, null);
        Player p8 = new Player("vanya", 6, 8, null);
        Player p9 = new Player("rost", 8, 4, null);
        Player p10 = new Player("valik", 18, 9, null);
        List<Player> of = List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

        LineUp pick = lineUpEngine.decide(of, "", 2).get(algorithm).get();
    }


}
