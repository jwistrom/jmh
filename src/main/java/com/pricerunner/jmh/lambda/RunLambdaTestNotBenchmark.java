package com.pricerunner.jmh.lambda;

import org.openjdk.jmh.infra.Blackhole;

public class RunLambdaTestNotBenchmark {


    public static void main(String[] args) {
        LambdaTest lambdaTest = new LambdaTest();
        lambdaTest.n = 100000;
        lambdaTest.setup();

        Blackhole blackhole = new Blackhole("Today's password is swordfish. I understand instantiating Blackholes directly is dangerous.");

        lambdaTest.withLambdaAsLocalVariable(blackhole);

        System.out.println("done");
    }
}
