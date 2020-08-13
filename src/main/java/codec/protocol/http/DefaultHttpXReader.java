package codec.protocol.http;

import codec.XReader;
import core.XBuffer;
import lombok.Data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2020/8/4 16:47
 **/
@Data
public class DefaultHttpXReader implements XReader {

    private List<Request> requests;

    private XBuffer readerBuffer;

    public DefaultHttpXReader() {
        requests = new ArrayList<>();
        readerBuffer = new XBuffer();
    }

    @Override
    public void read(ByteBuffer src) throws IOException {
        // ByteBuffer  -> contains the bytes read from NIO read
        readerBuffer.cache(src);

        Request request = HttpUtil.tryToParseHttpRequest(readerBuffer);
        while (request != null) {
            requests.add(request);
            request = HttpUtil.tryToParseHttpRequest(readerBuffer);
        }

    }

    @Override
    public List<XBuffer> getProtocolSpecialisedBufferBlocks() {
        if (requests.size() == 0) return new ArrayList<>();

        return requests.stream()
                .map(this::toXBuffer)
                .collect(Collectors.toList());
    }

    private XBuffer toXBuffer(Request request) {
        XBuffer xBuffer = new XBuffer();
        xBuffer.cache(request.getContent());
        xBuffer.setXSocketId(request.getSocketId());
        return xBuffer;
    }
}
