package global;

import core.XBuffer;
import core.XSocket;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Global Configuration across multiple components
 *
 * @Author: Yukai
 * Description: master T
 * create time: 2020/7/31 14:44
 **/
public class Config {

    public static final int QUEUE_CAPACITY = 1024;

    public static final Queue<XSocket> INBOUND_QUEUE = new ArrayBlockingQueue<XSocket>(QUEUE_CAPACITY);

    public static final Queue<XBuffer> OUTBOUND_QUEUE = new ArrayBlockingQueue<XBuffer>(QUEUE_CAPACITY);

    public static int PORT = 8080;

    public static AtomicInteger COUNT = new AtomicInteger(0);

    public static int X_BUFFER_INITIAL_SIZE = 4 * 1024; //4KB

    public static int BYTE_BUFFER_INITIAL_SIZE = 4 *1024; //4KB

    /**
     * race safely
     */
    public static String getXSocketId() {
        return "core.XSocket@" + COUNT.getAndIncrement();
    }
}
