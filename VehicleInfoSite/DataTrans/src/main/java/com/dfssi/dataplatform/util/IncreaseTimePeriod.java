package com.dfssi.dataplatform.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IncreaseTimePeriod {
    private static AtomicInteger connectPeriodTag = new AtomicInteger(0);

    private static int connectTaskPeriod;

    public static int randomConnectPeriod() {
        int start = (int) (Math.pow(2, connectPeriodTag.get()) * 1000);
        int end = (int) (Math.pow(2, connectPeriodTag.get() + 1) * 1000);
        connectTaskPeriod = (int)(start+Math.random()*(end));
        if (connectTaskPeriod >= 20 * 60 * 1000) {
            connectTaskPeriod = 20 * 60 * 1000;
        }else {
            connectPeriodTag.incrementAndGet();
        }
        return connectTaskPeriod;
    }
    public static void setNew(){
        connectPeriodTag.set(1);
    }

    public static void main(String[] args) {
        for (int i = 0 ;i< 1000;i++){
            int time = IncreaseTimePeriod.randomConnectPeriod();
            System.out.println(time);
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
