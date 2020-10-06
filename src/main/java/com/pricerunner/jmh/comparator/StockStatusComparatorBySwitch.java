package com.pricerunner.jmh.comparator;

import java.util.Comparator;

public class StockStatusComparatorBySwitch implements Comparator<StockStatus> {

    public static final StockStatusComparatorBySwitch INSTANCE = new StockStatusComparatorBySwitch();

    @Override
    public int compare(final StockStatus o1, final StockStatus o2) {
        if (o1 == o2) {
            return 0;
        }

        return Integer.compare(getOrder(o1), getOrder(o2));
    }

    private static final Integer getOrder(final StockStatus status) {
        switch (status) {
        case IN_STOCK:
            return 0;
        case PREORDER:
            return 1;
        case SPECIAL_ORDER:
            return 2;
        case OUT_OF_STOCK:
            return 3;
        case UNKNOWN:
            return 4;
        }

        throw new IllegalArgumentException("Invalid status: " + status);
    }

}
