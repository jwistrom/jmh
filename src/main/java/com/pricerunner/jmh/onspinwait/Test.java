package com.pricerunner.jmh.onspinwait;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
//@Fork(value = 1, jvmArgs = {"-XX:CompileThreshold=5000", "-Xmx500m", "-XX:+UnlockDiagnosticVMOptions", "-XX:+LogCompilation", "-XX:+TraceClassLoading", "-XX:+PrintAssembly"})
@Fork(value = 1)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
public class Test {

    @Param({"1000"})
    private int iterations;

    @Benchmark
    public void sleep(Blackhole bh) throws InterruptedException {
        int i = 0;
        int sum = 0;
        while(i++ < iterations) {
            Thread.sleep(1);
            sum += 1;
        }
        bh.consume(sum);
    }

    @Benchmark
    public void yield(Blackhole bh) {
        int i = 0;
        int sum = 0;
        while(i++ < iterations) {
            Thread.yield();
            sum += 1;
        }
        bh.consume(sum);
    }

    @Benchmark
    public void onSpinWait(Blackhole bh) {
        int i = 0;
        int sum = 0;
        while(i++ < iterations) {
            Thread.onSpinWait();
            sum += 1;
        }
        bh.consume(sum);
    }

}
