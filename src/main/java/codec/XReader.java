package codec;

import core.XBuffer;

import java.nio.ByteBuffer;

/**
 * <p>
 * date : 2020/7/31
 * time : 16:20
 * </p>
 *
 * @author Master T
 */
public interface XReader {

    void read(ByteBuffer src, XBuffer desc);

}
