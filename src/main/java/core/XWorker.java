package core;

import com.sun.tools.javac.util.Assert;
import ext.XHandler;
import global.Container;

import java.io.IOException;
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

public class XWorker implements Runnable {


    private XHandler handler;
    private Map<String, XSocket> connectedSocketsMap;

    private Selector readSelector;
    private Selector writeSelector;



    public XWorker() throws IOException {
        this.connectedSocketsMap = new ConcurrentHashMap<>();
        this.readSelector = Selector.open();
        this.writeSelector = Selector.open();
    }


    @Override
    public void run() {

        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
                registerAllAcceptedSockets();
                readReadySockets();
                /****************writing part****************************/
                registerSockesForWriting();
                writeToReadyChannel();

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
    private void registerAllAcceptedSockets() throws IOException {
        XSocket socket = Container.INBOUND_QUEUE.poll();

        while (socket != null) {
            SocketChannel socketChannel = socket.getSocketChannel();
            SelectionKey sk = socketChannel.register(readSelector, SelectionKey.OP_READ);

            sk.attach(socket);

            this.connectedSocketsMap.put(socket.getxSocketId(), socket);

            socket = Container.INBOUND_QUEUE.poll();
        }
    }

    private void readReadySockets() throws IOException {
        int selectedCount = readSelector.selectNow();

        if (selectedCount > 0) {
            Set<SelectionKey> selectionKeys = readSelector.selectedKeys();

            Iterator<SelectionKey> it = selectionKeys.iterator();
            while (it.hasNext()) {

                SelectionKey nextSelectionKey = it.next();

                XSocket xSocket = (XSocket) nextSelectionKey.attachment();

                xSocket.read();

                List<XBuffer> completeMsgBufferBlocks = xSocket.getxParser()
                        .getOutputs();

                if (completeMsgBufferBlocks.size() > 0) {
                    //handle the complete request(s) from a NIO channel read
                    if (handler != null) {

                        completeMsgBufferBlocks
                                .stream()
                                .map(handler::handle)
                                .forEach(Container.OUTBOUND_QUEUE::offer);
                    }
                }
            }
        }
    }

    /**********************************write********************************************************************/

    private void registerSockesForWriting() throws ClosedChannelException {
        XBuffer completeMsgBufferBlock = Container.OUTBOUND_QUEUE.poll();

        while (completeMsgBufferBlock != null) {
            String xSocketId = completeMsgBufferBlock.getxSocketId();
            XSocket associatedSocket = connectedSocketsMap.get(xSocketId);

            Assert.checkNonNull(associatedSocket);

            SelectionKey sk = associatedSocket.getSocketChannel()
                    .register(writeSelector, SelectionKey.OP_WRITE);
            sk.attach(associatedSocket);

            associatedSocket
                    .enqueue(completeMsgBufferBlock);

            completeMsgBufferBlock = Container.OUTBOUND_QUEUE.poll();
        }
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
                writableXSocket.write();
                it.remove();
            }

            selectionKeys.clear();
        }
    }

}
