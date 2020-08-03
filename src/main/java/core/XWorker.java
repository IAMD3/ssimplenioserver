package core;

import com.sun.tools.javac.util.Assert;
import ext.XHandler;
import global.Config;
import lombok.Data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2020/8/3 16:52
 **/
@Data
public class XWorker implements Runnable {


    private XHandler handler;
    private Map<String, XSocket> socketMap;

    private Selector readSelector;
    private Selector writeSelector;

    private ByteBuffer readMediator;
    private ByteBuffer writeMediator;

    public XWorker() throws IOException {
        this.socketMap = new ConcurrentHashMap<>();
        this.readSelector = Selector.open();
        this.writeSelector = Selector.open();

        this.readMediator = ByteBuffer.allocate(Config.BYTE_BUFFER_INITIAL_SIZE);
        this.writeMediator = ByteBuffer.allocate(Config.BYTE_BUFFER_INITIAL_SIZE);
    }


    @Override
    public void run() {

        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
                registerAllAcceptedChannelsFromQueue();
                readFromRegisteredXSocket();

                /****************writing part****************************/
                writeToSocketsFromOutboundQueue();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Biz Extension  point
     *
     * @param handler
     */
    public void setHandler(XHandler handler) {
        this.handler = handler;
    }

    /***********************************************************************************/
    private void offerToQueue(Queue desc, XBuffer xBuffer) {
        desc.add(xBuffer);
    }

    private void registerAllAcceptedChannelsFromQueue() throws IOException {
        XSocket socket = Config.INBOUND_QUEUE.poll();

        while (socket != null) {
            SocketChannel socketChannel = socket.getSocketChannel();
            SelectionKey sk = socketChannel.register(readSelector, SelectionKey.OP_READ);

            sk.attach(socket);

            this.socketMap.put(socket.getXSocketId(), socket);

            socket = Config.INBOUND_QUEUE.poll();
        }
    }

    private void readFromRegisteredXSocket() throws IOException {
        int selectedCount = readSelector.selectNow();

        if (selectedCount > 0) {
            Set<SelectionKey> selectionKeys = readSelector.selectedKeys();

            Iterator<SelectionKey> it = selectionKeys.iterator();
            while (it.hasNext()) {

                SelectionKey nextSelectionKey = it.next();

                XSocket xSocket = (XSocket) nextSelectionKey.attachment();

                xSocket.read(readMediator);

                List<XBuffer> completeMsgBufferBlocks = xSocket.getXReader()
                        .getProtocolSpecialisedBufferBlocks();

                if (completeMsgBufferBlocks.size() > 0) {
                    //handle the complete requests from a NIO channel read
                    if (handler != null) {
                        //note: it is a handler's responsibility to offer a complete message buffer block to outbound queue
                        handler.handle(xSocket);
                    }
                }
            }
        }
    }

    /**********************************write********************************************************************/
    private void writeToSocketsFromOutboundQueue() throws IOException {
        registerChannelForWriting();
        writeToReadyChannel();
    }


    private void writeToReadyChannel() throws IOException {
        int selectedCount = writeSelector.selectNow();

        if (selectedCount > 0) {
            Set<SelectionKey> selectionKeys = writeSelector.selectedKeys();
            Iterator<SelectionKey> it = selectionKeys.iterator();
            while (it.hasNext()){
                SelectionKey selectionKey = it.next();
                XSocket writableXSocket
                        = (XSocket) selectionKey.attachment();
                writableXSocket.write(writeMediator);
                it.remove();
            }

            selectionKeys.clear();
        }
    }

    private void registerChannelForWriting() throws ClosedChannelException {
        XBuffer completeMsgBufferBlock = Config.OUTBOUND_QUEUE.poll();

        while (completeMsgBufferBlock != null) {
            String xSocketId = completeMsgBufferBlock.getXSocketId();
            XSocket associatedSocket = socketMap.get(xSocketId);

            Assert.checkNonNull(associatedSocket);

            SelectionKey sk = associatedSocket.getSocketChannel()
                    .register(writeSelector, SelectionKey.OP_WRITE);
            sk.attach(associatedSocket);

            associatedSocket.getXWriter()
                    .enqueue(completeMsgBufferBlock);

            completeMsgBufferBlock = Config.OUTBOUND_QUEUE.poll();
        }
    }
}
