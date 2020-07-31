package codec;

import java.nio.ByteBuffer;

/**
 * <p>
 * date : 2020/7/31
 * time : 16:39
 * </p>
 *
 * @author Master T
 */
public interface XWriter {
    void write(ByteBuffer src, ByteBuffer desc);
}
