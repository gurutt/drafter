package org.gurutt.drafter.service;

import io.vavr.collection.List;
import org.gurutt.drafter.domain.LineUp;
import org.gurutt.drafter.domain.Player;
import org.junit.Test;

public class LineUpEngineTest {

    private LineUpEngine lineUpEngine = new LineUpEngine();
    @Test
    public void pickHappyPath() {

        List<Player> of = getPlayers();

        LineUp pick = lineUpEngine.decide(of).get(0);
        System.out.println(pick);
    }

    @Test
    public void pickNotHappyPath() {

        List<Player> of = getPlayers();

        LineUp pick = lineUpEngine.decide(of).get(0);
        System.out.println(pick);
    }

    @Test
    public void mockDraft() {

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

        LineUp pick = lineUpEngine.decide(of).get(0);
        System.out.println(pick.getWest());
        System.out.println(pick.getEast());
    }

    private List<Player> getPlayers() {
        Player yura = new Player("yura", 14, 10);
        Player serega = new Player("serega", 9, 9);
        Player roma = new Player("roma", 4, 4);
        Player lalka = new Player("lalka", 3, 2);
        return List.of(yura, serega, roma, lalka);
    }
}