package codec.protocol.http;

import codec.XWriter;
import core.XBuffer;
import global.Config;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2020/8/4 16:47
 **/
public class DefaultHttpXWriter implements XWriter {

    private Queue<XBuffer> respQueue;

    private XBuffer inFlyRespBuffer;

    private int processingRespOffset;

    public DefaultHttpXWriter() {
        respQueue = new ArrayBlockingQueue<XBuffer>(Config.QUEUE_CAPACITY);
        processingRespOffset = 0;
    }


    /**
     * NIO 写带来不确定性:
     * 一轮写操作:
     * 1. 写了完整一个业务数据包 -> Queue中取下一个业务数据包 本次写操作
     * 2. 写了部分业务数据包 -> 记录位置...下次对应NIO channel为可写状态的时候resume
     * @throws IOException
     */
    @Override
    public void write(ByteBuffer mediator, SocketChannel desc) throws IOException {
        if (inFlyRespBuffer == null) return;

        inFlyRespBuffer.pureContent();
        mediator.put(inFlyRespBuffer.getContent()
                , processingRespOffset
                , inFlyRespBuffer.getLength() - processingRespOffset);
        mediator.flip();

        int bytesWritten = desc.write(mediator);

        while (/**if able to write to NIO associated channel**/bytesWritten > 0
                && /**if finishing writing a response**/mediator.hasRemaining()) {
            bytesWritten = desc.write(mediator);
            processingRespOffset += bytesWritten;
        }

        if(/**a complete msg written**/processingRespOffset == inFlyRespBuffer.getLength()){
            inFlyRespBuffer = respQueue.poll();
            processingRespOffset = 0;
        }

        mediator.clear();

    }

    @Override
    public void enqueue(XBuffer xBuffer) {

        if (inFlyRespBuffer == null) {
            inFlyRespBuffer = xBuffer;
        } else {
            respQueue.offer(xBuffer);
        }

    }
}
