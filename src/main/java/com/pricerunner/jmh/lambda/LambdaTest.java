package com.pricerunner.jmh.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
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
@Fork(value = 1, jvmArgs = {"-XX:CompileThreshold=5000", "-XX:+UnlockDiagnosticVMOptions",/* "-XX:+PrintAssembly",*/"-XX:+LogCompilation", "-XX:+TraceClassLoading"})
@Warmup(iterations = 2)
public class LambdaTest {

    @Param({"10000"})
    public int n;

    private List<Integer> data;

    @Setup
    public void setup(){
        data = createData();
    }

    @Benchmark
    public void withLambdaAsLocalVariable(Blackhole blackhole){

        Queue queue = new Queue(data);
        Processor processor = new Processor(blackhole, queue);

        processor.processInts();
    }

    @Benchmark
    public void withLambdaAsField(Blackhole blackhole){

        Queue queue = new Queue(data);
        OptimizedProcessor processor = new OptimizedProcessor(blackhole, queue);

        processor.processInts();
    }

    private List<Integer> createData() {
        final Random random = new Random();
        final List<Integer> data = new ArrayList<>();
        for (int i=0 ; i<n ; i++){
            data.add(random.nextInt(n));
        }
        return data;
    }


    public static void main(String[] args) throws Exception {
        final Options opt = new OptionsBuilder()
            .include(LambdaTest.class.getSimpleName())
            .build();

        new Runner(opt).run();
    }

    @CompilerControl(value = CompilerControl.Mode.DONT_INLINE)
    class Processor {

        Blackhole blackhole;
        Queue queue;

        public Processor(Blackhole blackhole, Queue queue) {
            this.blackhole = blackhole;
            this.queue = queue;
        }

        public void processInts(){
            queue.read(i -> {
                if (i%2 == 0)
                    doubleEvenNumberAndConsume(i);
            });
        }

        public void doubleEvenNumberAndConsume(Integer evenNumber){
            blackhole.consume(evenNumber * 2);
        }

    }

    @CompilerControl(value = CompilerControl.Mode.DONT_INLINE)
    class OptimizedProcessor {

        Blackhole blackhole;
        Queue queue;
        Consumer<Integer> handler;

        public OptimizedProcessor(Blackhole blackhole, Queue queue) {
            this.blackhole = blackhole;
            this.queue = queue;
            this.handler = i -> {
                if (i%2 == 0)
                    doubleEvenNumberAndConsume(i);
            };
        }

        public void processInts(){
            queue.read(handler);
        }

        public void doubleEvenNumberAndConsume(Integer evenNumber){
            blackhole.consume(evenNumber * 2);
        }

    }

    @CompilerControl(value = CompilerControl.Mode.DONT_INLINE)
    class Queue {

        List<Integer> ints;

        public Queue(List<Integer> ints) {
            this.ints = ints;
        }

        public void read(Consumer<Integer> consumer){
            ints.forEach(consumer);
        }

    }



}
