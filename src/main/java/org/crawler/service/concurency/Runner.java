package org.crawler.service.concurency;

class Incrementer {
    public int counter;

    public synchronized void incrementCounter() {
        counter ++;
    }
}


class CountIncrementer implements Runnable {
    Incrementer i;
    int maxIncrementValue;

    CountIncrementer(int maxIncrementValue) {
        this.maxIncrementValue = maxIncrementValue;
        this.i = new Incrementer();
    }
    @Override
    public void run() {
        while (i.counter < this.maxIncrementValue) {
            i.incrementCounter();
            System.out.println(i.counter);
        }
    }
}

public class Runner {
    public static void main(String[] args)  {
        CountIncrementer counter = new CountIncrementer(1000);
        Thread a1 = new Thread(counter);

        Thread a2 = new Thread(counter);
        a1.start();
        a2.start();
    }
}

