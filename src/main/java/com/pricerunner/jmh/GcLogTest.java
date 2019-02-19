package com.pricerunner.jmh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
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
@Fork(value = 1, jvmArgs = {"-XX:CompileThreshold=5000", "-XX:+PrintGCDetails", "-Xloggc:/Users/johanwistrom/tmp/test-gc_%p.log"})
@Warmup(iterations = 1)
@Measurement(iterations = 1)
public class GcLogTest {

    @Param({"100", "10000"})
    private int n;

    private List<Integer> data;

    @Setup
    public void setup(){
        data = createData();
    }

    @Benchmark
    public void streamWithMethodReference(final Blackhole blackhole){
        int max = data.stream().mapToInt(i -> i).reduce(Integer.MIN_VALUE, Integer::max);
        blackhole.consume(max);
    }


    public static void main(String[] args) throws Exception {
        final Options opt = new OptionsBuilder()
            .include(GcLogTest.class.getSimpleName())
            .build();

        new Runner(opt).run();
    }



    private List<Integer> createData() {
        final Random random = new Random();
        final List<Integer> data = new ArrayList<>();
        for (int i=0 ; i<n ; i++){
            data.add(random.nextInt(n));
        }
        return data;
    }

}
