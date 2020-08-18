package codec.protocol.http;

import codec.XParser;
import core.XBuffer;

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
public class DefaultHttpXParser implements XParser {

    private List<Request> requests;

    private XBuffer readerBuffer;

    public DefaultHttpXParser(String socketId) {
        requests = new ArrayList<>();
        readerBuffer = new XBuffer();

        readerBuffer.setxSocketId(socketId);
    }

    @Override
    public void parse(ByteBuffer src) throws IOException {
        // ByteBuffer  -> contains the bytes read from NIO read
        readerBuffer.cache(src);

        Request request = HttpUtil.tryToParseHttpRequest(readerBuffer);
        while (request != null) {
            requests.add(request);
            request = HttpUtil.tryToParseHttpRequest(readerBuffer);
        }

    }

    @Override
    public List<XBuffer> getOutputs() {
        if (requests.size() == 0) return new ArrayList<>();

        return requests.stream()
                .map(this::toXBuffer)
                .collect(Collectors.toList());
    }

    private XBuffer toXBuffer(Request request) {
        XBuffer xBuffer = new XBuffer();
        xBuffer.cache(request.getContent());
        xBuffer.setxSocketId(request.getSocketId());
        return xBuffer;
    }
}
