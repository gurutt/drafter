package org.gurutt.drafter.performance;

import io.vavr.collection.List;
import org.gurutt.drafter.domain.Player;
import org.gurutt.drafter.service.Drafter;
import org.gurutt.drafter.service.SlotSwapDrafter;
import org.gurutt.drafter.service.TestPlayerData;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import static org.gurutt.drafter.service.TestPlayerData.*;

@State(Scope.Benchmark)
public class ExecutionPlan {

    public List<Player> players;

    Drafter drafter = new SlotSwapDrafter();

    @Setup(Level.Invocation)
    public void setUp() {

        players = TestPlayerData.load();

        players = players(VALIK, VANYA, REUS, NIKITA, YURA, ROMA, KOLYA, DIMONR, ROST, IGOR);
    }
}
