package org.gurutt.drafter.service;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.gurutt.drafter.service.LineUpEngine.SKILL;
import static org.gurutt.drafter.service.LineUpEngine.STAMINA;
import static org.junit.Assert.assertTrue;

class LineUpEngineTest {

    private static final String YURA = "yura";
    private static final String SEREGA = "serega";
    private static final String ROMA = "roma";
    private static final String LALKA = "lalka";
    private LineUpEngine lineUpEngine = new LineUpEngine();

    @Test
    void testTwoOnTwo() {

        // given
        List<Player> players = getPlayers();

        // when
        Map<String, LineUp> rosters = lineUpEngine.decide(players);
        LineUp skills = rosters.get(SKILL).get();

        // then
        List<String> westRoster = skills.getWest().getPlayers().map(Player::getSlug);
        List<String> eastRoster = skills.getEast().getPlayers().map(Player::getSlug);
        assertTrue(westRoster.containsAll(Arrays.asList(YURA, LALKA)));
        assertTrue(eastRoster.containsAll(Arrays.asList(ROMA, SEREGA)));
        assertTrue(rosters.containsKey(STAMINA));
    }

    @Test
    public void testBigGame() {

        Player p1 = new Player("yura",14, 10);
        Player p2 = new Player("roshin", 11, 9);
        Player p3 = new Player("igor", 16, 9);
        Player p4 = new Player("kolya", 15, 8);
        Player p5 = new Player("reus", 9, 6);
        Player p6 = new Player("nikita", 9, 8);
        Player p7 = new Player("roma", 11, 7);
        Player p8 = new Player("vanya", 6, 8);
        Player p9 = new Player("rost", 8, 4);
        Player p10 = new Player("valik", 18, 9);
        List<Player> of = List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

        LineUp pick = lineUpEngine.decide(of).get(SKILL).get();
        System.out.println(pick.getWest());
        System.out.println(pick.getEast());
    }

    private List<Player> getPlayers() {
        Player yura = new Player(YURA, 14, 10);
        Player serega = new Player(SEREGA, 9, 9);
        Player roma = new Player(ROMA, 4, 4);
        Player lalka = new Player(LALKA, 3, 2);
        return List.of(yura, serega, roma, lalka);
    }
}