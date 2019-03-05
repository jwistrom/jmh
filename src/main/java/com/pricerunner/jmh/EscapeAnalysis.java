package com.pricerunner.jmh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-XX:CompileThreshold=5000", "-Xmx500m", "-XX:+UnlockDiagnosticVMOptions", "-XX:+LogCompilation", "-XX:+TraceClassLoading", "-XX:+PrintAssembly"})
@Warmup(iterations = 2)
@Measurement(iterations = 5)
public class EscapeAnalysis {

    @Param({"1000000"})
    private int n;

    private List<Integer> data;

    @Setup
    public void setup() {
        data = createData();
    }

    @Benchmark
    public void noBlackhole(Blackhole blackhole) throws InterruptedException {
        int x = 1;
        for (Integer i : data) {
            doSomethingHeavy();
        }
        blackhole.consume(x);
    }

    @Benchmark
    public void withBlackhole(Blackhole blackhole) throws InterruptedException {
        int x = 1;
        for (Integer i : data) {
            doSomethingHeavyWithBlackhole(blackhole);
        }
        blackhole.consume(x);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private void doSomethingHeavyWithBlackhole( Blackhole blackhole) throws InterruptedException {
        int a = 1;
        int b = 2;
        int sum = a + b;
        blackhole.consume(sum);
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private void doSomethingHeavy() throws InterruptedException {
        int a = 1;
        int b = 2;
        int sum = a + b;
    }


    public static void main(String[] args) throws Exception {
        final Options opt = new OptionsBuilder()
            .include(EscapeAnalysis.class.getSimpleName())
            .build();

        new Runner(opt).run();
    }


    private List<Integer> createData() {
        final Random random = new Random();
        final List<Integer> data = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            data.add(random.nextInt(n));
        }
        return data;
    }

}
