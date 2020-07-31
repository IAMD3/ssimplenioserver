package core;

import com.sun.tools.javac.util.Assert;
import global.Config;
import lombok.Data;

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
 *
 * 安全 -> 扩展/释放
 * create time: 2020/7/31 15:07
 **/
@Data
public class XBuffer {

    private final int READY = 0;

    private final int PROCESSING = 1;

    /**
     * internal content
     */
    private byte[] content;

    /**
     * logical length of internal content
     */
    private volatile int length;

    /**
     * 0: ready for action
     * 1: in the processing of action
     */
    private AtomicInteger flag = new AtomicInteger(READY);


    public XBuffer() {
        content = new byte[Config.BUFFER_INITIAL_SIZE];
        length = 0;
    }

    public void expend2Double(){
        raceSafely(this::doExpend2Double);
    }

    public void trim(int offset){
        final int do_offset = offset;
        raceSafely(()-> doTrim(do_offset));
    }


    /************************internal methods*************************************************/
    private void doExpend2Double() {
        byte[] desc = new byte[content.length * 2];
        System.arraycopy(content, 0, desc, 0, length);
        content = desc;
    }

    private void doTrim(int offset) {
        Assert.check(content.length > offset, "content length must bigger than offset");

        byte[] desc = new byte[content.length - offset];
        System.arraycopy(content, 0, desc, 0, length);
        content = desc;
    }

    private void raceSafely(Runnable action) {
        try {
            if (flag.compareAndSet(READY, PROCESSING)) {
                action.run();
            }
        } finally {
            flag.compareAndSet(PROCESSING, READY);
        }

    }
}
