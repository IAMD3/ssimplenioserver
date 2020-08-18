package core;

import codec.CodeCFactory;
import codec.XParser;
import codec.XWriter;
import global.Container;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2020/7/31 14:54
 **/
public class XSocket {


    private String xSocketId;


    private SocketChannel socketChannel;

    private XBuffer readBuffer;

    private XBuffer writeBuffer;


    /**
     * common reader & writer -> should be adaptable for various protocols
     **/
    private XParser xParser;

    private XWriter xWriter;

    private boolean init;

    public XSocket(SocketChannel sc) {
        this.socketChannel = sc;

        this.xSocketId = Container.getXSocketId();
        this.readBuffer = new XBuffer();
        this.writeBuffer = new XBuffer();

        init = false;
    }

    public void initCodeC(CodeCFactory factory) {
        this.xParser = factory.createXReader(this.xSocketId);
        this.xWriter = factory.createXWriter();

        init = true;
    }


    public void write() throws IOException {
        xWriter.write(Container.writeMediator, socketChannel);
    }


    public void read() throws IOException {
        ByteBuffer mediator =  Container.readMediator;
        //a reading attempt
        int byteRead = fillReadingByteBuffer(mediator);
        if (byteRead == 0) return;

        mediator.flip();
        xParser.parse(mediator);
    }


    /**
     * cannot know how many bytes read from
     * java.nio.channels.SocketChannel#read(java.nio.ByteBuffer)
     * so... keeping reading till no thing coming out
     *
     * @param mediator
     * @return totalBytes read
     * @throws IOException
     */
    public int fillReadingByteBuffer(ByteBuffer mediator) throws IOException {
        int bytesRead = socketChannel.read(mediator);
        int totalBytesRead = bytesRead;

        while (bytesRead > 0) {
            bytesRead = socketChannel.read(mediator);
            totalBytesRead += bytesRead;
        }

        return totalBytesRead;
    }

    /**
     * called by implementation of codec.XWriter
     * cannot know how many bytes written from
     * java.nio.channels.SocketChannel#write(java.nio.ByteBuffer)
     * so.... keeping writing till no thing can write any more
     *
     * @return total Bytes written
     */
    public int subCall_keepWritingToChannel(ByteBuffer mediator) throws IOException {
        int bytesWritten = socketChannel.write(mediator);
        int totalBytesWritten = bytesWritten;

        while (bytesWritten > 0) {
            bytesWritten = socketChannel.write(mediator);
            totalBytesWritten += bytesWritten;
        }

        return totalBytesWritten;
    }



    public String getxSocketId() {
        return xSocketId;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public XWriter getxWriter() {
        return xWriter;
    }

    public XParser getxParser() {
        return xParser;
    }

}
