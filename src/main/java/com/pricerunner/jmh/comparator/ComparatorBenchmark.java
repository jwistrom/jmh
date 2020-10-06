package com.pricerunner.jmh.comparator;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
//@Fork(value = 1, jvmArgs = {"-XX:CompileThreshold=50000", "-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintAssembly", "-XX:+LogCompilation", "-XX:+TraceClassLoading"})
@Fork(value = 1)
@Warmup(iterations = 2)
public class ComparatorBenchmark {

    @Param({ "100", "1000", "1000000" })
    private int n;

    private List<StockStatus> stockStatusesToSort;

    @Setup
    public void setup() {
        stockStatusesToSort = createData();
    }

    @Benchmark
    public void comparatorWithMap() {
        stockStatusesToSort.sort(StockStatusComparatorByMap.INSTANCE);
    }

    @Benchmark
    public void comparatorWithSwitch() {
        stockStatusesToSort.sort(StockStatusComparatorBySwitch.INSTANCE);
    }

    private List<StockStatus> createData() {
        return IntStream.range(0, n)
            .map(i -> i % StockStatus.values().length)
            .mapToObj(i -> StockStatus.values()[i])
            .collect(Collectors.toCollection(ArrayList::new));
    }

}
