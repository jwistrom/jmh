package com.pricerunner.jmh;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Fork;
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
@Fork(value = 1, jvmArgs = {"-XX:CompileThreshold=50000"})
@Warmup(iterations = 3)
public class LambdaOrMethodReference {

    @Param({"10000", "100000", "1000000"})
    private int n;

    private List<Integer> data;

    @Setup
    public void setup(){
        data = createData();
    }


    @Benchmark
    public void mapByLambda(final Blackhole blackhole){
        int sum = data.stream().mapToInt(i -> i + 2 * i).sum();
        blackhole.consume(sum);
    }

    @Benchmark
    public void mapByMethodReferenceNotInlined(final Blackhole blackhole){
        int sum = data.stream().mapToInt(this::mapNotInlined).sum();
        blackhole.consume(sum);
    }

    @Benchmark
    public void mapByMethodReferenceInlined(final Blackhole blackhole){
        int sum = data.stream().mapToInt(this::mapInlined).sum();
        blackhole.consume(sum);
    }

    @CompilerControl(CompilerControl.Mode.INLINE)
    private int mapInlined(final Integer i){
        return i + 2*i;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private int mapNotInlined(final Integer i){
        return i + 2*i;
    }


    public static void main(String[] args) throws Exception {
        final Options opt = new OptionsBuilder()
            .include(LambdaOrMethodReference.class.getSimpleName())
            .build();

        new Runner(opt).run();
    }



    private List<Integer> createData() {
        final List<Integer> data = new ArrayList<>();
        for (int i=0 ; i<n ; i++){
            data.add(i);
        }
        return data;
    }

}
