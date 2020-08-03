package queue;

import core.XSocket;
import global.Config;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2020/8/3 17:21
 **/
public class QueueTest {

    public static void main(String[] args){
        Queue<XSocket> queue = Config.INBOUND_QUEUE;


        new Thread(QueueTest::pop)
                .start();
        new Thread(QueueTest::appendingQueue)
                .start();


    }

    private static void pop(){
        Queue<XSocket> queue = Config.INBOUND_QUEUE;
        while (true){
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(queue.poll());
        }
    }

    private static void appendingQueue(){
        Queue<XSocket> queue = Config.INBOUND_QUEUE;

        while(true){
            try {
                TimeUnit.SECONDS.sleep(1);
                queue.offer(new XSocket(null));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
