package com.alibaba;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class TestCondition {

    Lock lock = new ReentrantLock();
    Condition condition =  lock.newCondition();
    Condition condition1 =  lock.newCondition();

    static CountDownLatch countDownLatch = new CountDownLatch(1);

    public void runingCreate(){
        countDownLatch.countDown();
        while(true) {
            try {
                lock.lock();
                System.out.println("1");
                condition.await();
                System.out.println("3-1");
                condition1.signal();
                Thread.sleep(5000);
                lock.unlock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyCreate(){
        try{
            lock.lock();
            System.out.println("2");
            condition.signal();
            System.out.println("3-2");
            //boolean s = condition1.await(1,TimeUnit.SECONDS);
            //System.out.println("请求创建ok"+s);
            Thread.sleep(50000000);
            lock.unlock();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[]args) throws Exception{

        final TestCondition testCondition = new TestCondition();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                testCondition.runingCreate();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

        countDownLatch.await();
        ExecutorService executorService =  Executors.newFixedThreadPool(100);
        int i = 1;
        while(i>0){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    testCondition.notifyCreate();
                }
            });
            Thread.sleep(10);
            i--;
        }

        LockSupport.park();


    }

}
