package core;

import com.sun.tools.javac.util.Assert;
import global.Container;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Yukai
 * Description:
 * 一款简单的缓存设计:
 * 考虑点 -> A.弹性 B.性能 C.竞争
 * @see java.nio.ByteBuffer to XBuffer
 * NIO 的特征: 每次读取的数据不一定能decode成一个完整的业务数据包(粘包....拆包....)
 * 使用每个channel维护 读 & 写 缓存的方式解决
 * 读:配合特定协议下的业务数据包检查组件 -> 反序列化业务数据包
 * 写:配合记录写位置的指针 -> 下一次写事件时resume上次步骤
 * <p>
 * 安全 -> 扩展/释放
 * create time: 2020/7/31 15:07
 **/

public class XBuffer {


    /**
     * associated XSocketId
     */
    private String xSocketId;
    /**
     * internal content
     */
    private byte[] content;

    /**
     * logical length of internal content
     */
    private volatile int length;


    /**************************STATUS*******************************************/
    private final int READY = 0;

    private final int PROCESSING = 1;

    /**
     * 0: ready for action
     * 1: in the processing of action
     */
    private AtomicInteger flag = new AtomicInteger(READY);


    public XBuffer() {
        content = new byte[Container.X_BUFFER_INITIAL_SIZE];
        length = 0;
    }


    public void cache(byte[] src) {
        int remainBytes = src.length;

        while (length + remainBytes > content.length) {
            expend2Double();
        }

        System.arraycopy(src, 0, content, length, remainBytes);
        length += remainBytes;
    }

    public void cache(ByteBuffer byteBuffer) {
        int remainBytes = byteBuffer.remaining();

        while (length + remainBytes > content.length) {
            expend2Double();
        }

        byteBuffer.get(content, length, remainBytes);
        length += remainBytes;
    }

    public void reset() {
        content = new byte[Container.X_BUFFER_INITIAL_SIZE];
        length = 0;
    }

    public void expend2Double() {
        doExpend2Double();
    }


    /**
     * called after a successful de-serialisation
     *
     * @param offset
     */
    public void trim(Integer offset, Integer length) {
        final Integer do_offset = offset;
        final Integer do_length = length;
       doTrim(do_offset, do_length);
    }

    public void pureContent() {
        trim(0, length);
    }

    /************************internal methods*************************************************/
    private void doExpend2Double() {
        byte[] desc;
        if(content.length < Container.BYTE_BUFFER_INITIAL_SIZE){
            desc = new byte[Container.BYTE_BUFFER_INITIAL_SIZE];
        }else {
            desc =  new byte[content.length * 2];
        }


        System.arraycopy(content, 0, desc, 0, length);
        content = desc;
    }

    private void doTrim(Integer offset, Integer length) {
        Assert.check(content.length > offset, "content length must bigger than offset");

        byte[] desc = new byte[length];
        System.arraycopy(content, offset, desc, 0, length);
        content = desc;
        this.length = length;
    }





    public String getxSocketId() {
        return xSocketId;
    }

    public void setxSocketId(String xSocketId) {
        this.xSocketId = xSocketId;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

}
