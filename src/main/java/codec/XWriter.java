package codec;

import core.XBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * <p>
 * date : 2020/7/31
 * time : 16:39
 * </p>
 *
 * @author Master T
 */
public interface XWriter {

    void write(ByteBuffer src, SocketChannel desc) throws IOException;

    void enqueue(XBuffer xBuffer);

    boolean isEmpty();
}
