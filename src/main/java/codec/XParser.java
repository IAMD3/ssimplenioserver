package codec;

import core.XBuffer;

import java.io.IOException;
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
public interface XParser {

    void parse(XBuffer src) throws IOException;

    List<XBuffer> getOutputs();

    void clearOutputs();
}
