package codec;

import core.XBuffer;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * <p>
 * date : 2020/7/31
 * time : 16:20
 * </p>
 *
 * @author Master T
 */
public interface XReader {

    void read(ByteBuffer src);

    List<XBuffer> getProtocolSpecialisedBufferBlocks();
}
