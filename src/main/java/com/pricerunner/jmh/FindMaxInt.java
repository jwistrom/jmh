package com.pricerunner.jmh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.IntBinaryOperator;
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
@Fork(value = 1, jvmArgs = {"-XX:CompileThreshold=5000", "-XX:+UnlockDiagnosticVMOptions", "-XX:+LogCompilation", "-XX:+TraceClassLoading", "-XX:+PrintInlining"})
@Warmup(iterations = 2)
public class FindMaxInt {

    @Param({"1000000"})
    private int n;

    private List<Integer> data;

    @Setup
    public void setup(){
        data = createData();
    }
//
//    @Benchmark
//    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
//    public void streamWithMethodReference(final Blackhole blackhole){
//        int max = data.stream().mapToInt(i -> i).reduce(Integer.MIN_VALUE, Integer::max);
//        blackhole.consume(max);
//    }

//    @Benchmark
//    public void forLoop(final Blackhole blackhole){
//        int max = Integer.MAX_VALUE;
//        for (int i=0 ; i<data.size() ; i++){
//            max = Integer.max(max, data.get(i));
//        }
//        blackhole.consume(max);
//    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void streamWithLambda(final Blackhole blackhole){
        int max = data.stream().mapToInt(i -> i).reduce(Integer.MIN_VALUE, (i1, i2) -> Integer.max(i1, i2));
        blackhole.consume(max);
    }

//    @Benchmark
//    public void streamWithMethodReferenceParallel(final Blackhole blackhole){
//        int max = data.parallelStream().mapToInt(i -> i).reduce(Integer.MIN_VALUE, Integer::max);
//        blackhole.consume(max);
//    }

//    public static void main(String[] args) {
//        FindMaxInt findMaxInt = new FindMaxInt();
//        findMaxInt.n = 100000000;
//        findMaxInt.setup();
//
//        Blackhole blackhole = new Blackhole("Today's password is swordfish. I understand instantiating Blackholes directly is dangerous.");
//        findMaxInt.streamWithLambda(blackhole);
//    }


    public static void main(String[] args) throws Exception {
        final Options opt = new OptionsBuilder()
            .include(FindMaxInt.class.getSimpleName())
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
