package com.pricerunner.jmh;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
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
@Fork(value = 1, jvmArgs = {"-XX:CompileThreshold=5000", "-XX:+UnlockDiagnosticVMOptions", "-XX:+LogCompilation", "-XX:+TraceClassLoading", "-XX:+PrintAssembly"})
@Warmup(iterations = 2)
@Measurement(iterations = 5)
public class EscapeAnalysisObject {

    int x;

    @Benchmark
    public void blackhole(Blackhole blackhole) {
        MyObject o = new MyObject(x);
        blackhole.consume(o);
    }

    @Benchmark
    public void noBlackhole() {
        MyObject o = new MyObject(x);
        int i = o.x;

    }

    static class MyObject {
        final int x;
        MyObject(int x) {
            this.x = x;
        }
    }


    public static void main(String[] args) throws Exception {
        final Options opt = new OptionsBuilder()
            .include(EscapeAnalysisObject.class.getSimpleName())
            .build();

        new Runner(opt).run();
    }

}
