package com.pricerunner.jmh;

import java.util.function.Function;

public class Temp {

    private Integer fieldNumber = 2;

    public Temp() {
       test();
    }

    private void test(){

        int localNumber = 2;

        Function<Integer, Integer> sum1 = i -> i+localNumber;

        Function<Integer, Integer> sum2 = i -> i+ fieldNumber;

    }

}
