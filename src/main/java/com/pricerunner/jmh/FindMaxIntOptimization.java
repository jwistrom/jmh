package com.pricerunner.jmh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.IntBinaryOperator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
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
@Fork(value = 1, jvmArgs = {"-XX:CompileThreshold=5000"/*, "-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintAssembly", "-XX:+LogCompilation", "-XX:+TraceClassLoading"*/})
@Warmup(iterations = 2)
public class FindMaxIntOptimization {

    @Param({"10000", "1000000", "10000000"})
    private int n;

    private List<Integer> data;

    @Setup
    public void setup(){
        data = createData();
    }

    @Benchmark
    public void streamWithMethodReference(final Blackhole blackhole){
        int max = data.stream().mapToInt(Integer::intValue).reduce(Integer.MIN_VALUE, Integer::max);
        blackhole.consume(max);
    }

    @Benchmark
    public void forLoop(final Blackhole blackhole){
        final List<Integer> localData = data;

        int max = Integer.MAX_VALUE;
        for (int i=0 ; i<localData.size() ; i++){
            max = Integer.max(max, localData.get(i));
        }
        blackhole.consume(max);
    }

    @Benchmark
    public void streamWithLambda(final Blackhole blackhole){
        int max = data.stream().mapToInt(Integer::intValue).reduce(Integer.MIN_VALUE, (i1, i2) -> Integer.max(i1, i2));
        blackhole.consume(max);
    }

    @Benchmark
    public void streamWithSingletonLambda(final Blackhole blackhole){
        final IntBinaryOperator lambda = (i1, i2) -> Integer.max(i1, i2);
        int max = data.stream().mapToInt(Integer::intValue).reduce(Integer.MIN_VALUE, lambda);
        blackhole.consume(max);
    }

    @Benchmark
    public void streamWithMethodReferenceParallel(final Blackhole blackhole){
        int max = data.parallelStream().mapToInt(Integer::intValue).reduce(Integer.MIN_VALUE, Integer::max);
        blackhole.consume(max);
    }


    public static void main(String[] args) throws Exception {
        final Options opt = new OptionsBuilder()
            .include(FindMaxIntOptimization.class.getSimpleName())
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
