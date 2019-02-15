package org.gurutt.drafter.performance;

import org.gurutt.drafter.domain.DraftContext;
import org.gurutt.drafter.domain.Player;
import org.gurutt.drafter.service.Drafter;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Format;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Warmup;

public class PerformanceTest {

    @Benchmark
    @Fork(value = 1)
    @Warmup(iterations = 2)
    @BenchmarkMode(Mode.Throughput)
    public void draft(ExecutionPlan executionPlan) {

        Drafter drafter = executionPlan.drafter;
        drafter.decide(executionPlan.players, DraftContext.of(Player::getSkill));

    }
}
