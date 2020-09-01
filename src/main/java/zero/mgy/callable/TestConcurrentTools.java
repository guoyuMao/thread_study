package zero.mgy.callable;

import netscape.security.UserTarget;
import org.junit.Test;

import javax.sound.midi.Soundbank;
import java.util.concurrent.*;

public class TestConcurrentTools {

    @Test
    public void testCallable() throws ExecutionException, InterruptedException {
        ThreadPoolExecutor ex = new ThreadPoolExecutor(
                3,
                5,
                3,
                TimeUnit.MINUTES,
                new LinkedBlockingDeque<Runnable>(100)
        );
        Future<String> future = ex.submit(()->{
            {
                System.out.println("name: callable Name");
                Thread.sleep(3000l);
                return "callable return msg: callableTest Msg";
            }
        });
        System.out.println("hello");
        String retStr = future.get();
        System.out.println(retStr);
        ex.shutdown();
    }

    /**
     * CountDowanLatch 倒计数锁
     * 一般用于一个线程等待若干个其他线程执行完成任务之后，它才执行，不可重复使用；
     * 使用场景：等待其任务执行完之后再执行,通常用于一个线程等待多个线程
     * @throws InterruptedException
     */
    @Test
    public void testCountDownLatch() throws InterruptedException {
        CountDownLatch downLatch = new CountDownLatch(5);
        new Thread(() -> {
            System.out.println("thread 1 wait execute");
            try {
                downLatch.await(); //进入等待的状态
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("thread 1 executed");
        }).start();
        new Thread(() -> {
            System.out.println("thread 2 wait execute");
            try {
                downLatch.await(1l,TimeUnit.SECONDS); //进入等待的状态,最多等待1秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("thread 2 executed");
        }).start();

        System.out.println(downLatch.toString());

        for (int i = 0; i <= 10; i++) {
            Thread.sleep(300l);
            System.out.println("count down latch is : " + downLatch.getCount());
            downLatch.countDown();  //计数器减一
        }

        System.out.println("main thread is over!");
    }

    /**
     * 篱栅
     * 一般用于一组线程互相等待至某个状态，然后这一组线程再同时执行，可重用。
     * 允许一组线程相互等待达到一个共同的障碍点，之后再继续执行
     */
    @Test
    public void testCyclicBarrier(){
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5);
        for (int i = 0; i < 5; i++) {
            int x = i;
            new Thread(() -> {
                try {
                    Thread.sleep(x * 500L);
                    System.out.println("thread: " + x + " is OK! and parites is "+ cyclicBarrier.getParties()+"; and the wait number is "+ cyclicBarrier.getNumberWaiting());
                    cyclicBarrier.await();
                    System.out.println("thread: "+x+" is execute!");
                } catch (InterruptedException|BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        try {
            Thread.sleep(5000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("the main thread is Over!");
    }

    /**
     * 信号灯
     *
     * 控制并发数量
     * 控制同时访问特定资源的线程数量
     */
    @Test
    public void testSemaphore() throws InterruptedException {
        Semaphore semaphore = new Semaphore(5);
        for (int i = 0; i < 20; i++) {
            int x = i;
            new Thread(()->{
                try {
                    semaphore.acquire();
                    System.out.println("thread " + x + " is OK");
                    Thread.sleep(500l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("thread " + x + " is end");
                    semaphore.release();
                }
            }).start();
        }

        Thread.sleep(10000l);
    }

    @Test
    public void testExchanger(){
        Exchanger<String> exchanger = new Exchanger<>();
        String[] str = {"aa", "bb", "cc", "dd"};
        for (int i = 0; i < str.length; i++) {
            int x = i;
            new Thread(()->{
                System.out.println("thread " + x + " wait exchange: " + str[x]);
                try {
                    String value = exchanger.exchange(str[x]); //交换值
                    System.out.println("thread " + x + " value is : " + value);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        try {
            Thread.sleep(5000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
