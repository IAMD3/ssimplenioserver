package ext;

import core.XBuffer;
import core.XSocket;
import global.Config;

import java.util.List;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2020/8/3 20:19
 **/
public abstract class AbstractXHandler implements XHandler {
    private XHandler next;

    private boolean finished = false;

    /** abstract method implemented by sub-class
     * @param xSocket
     * @return
     */
    abstract List<XBuffer> doHandler(XSocket xSocket);

    /**template method
     * @param socket
     */
    @Override
    public void handle(XSocket socket) {
        List<XBuffer> outputs = doHandler(socket);

        if(finished){
            //clear buffers blocks from the readers
            List<XBuffer> bufferBlocks = socket.getXReader()
                    .getProtocolSpecialisedBufferBlocks();
            bufferBlocks.clear();
            //offer all output buffer blocks to a shared queue
            outputs.forEach(this::offerRespBlockToQueue);

            finishHandling();
        }
    }

    @Override
    public XHandler next() {
        return next;
    }

    public void finishHandling() {
        this.finished = true;
    }

    private void offerRespBlockToQueue(XBuffer respBlock){
        Config.OUTBOUND_QUEUE.offer(respBlock);
    }
}
