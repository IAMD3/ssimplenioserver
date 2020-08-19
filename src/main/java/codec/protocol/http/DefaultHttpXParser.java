package codec.protocol.http;

import codec.XParser;
import core.XBuffer;

import java.io.IOException;
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

    private boolean endOfStreamReached =false;


    public DefaultHttpXParser() {
        requests = new ArrayList<>();
    }

    @Override
    public void parse(XBuffer src) throws IOException {
        // XBuffer  -> contains the bytes read from NIO read
        Request request = HttpUtil.tryToParseHttpRequest(src);
        while (request != null) {
            requests.add(request);
            request = HttpUtil.tryToParseHttpRequest(src);
        }

    }

    @Override
    public List<XBuffer> getOutputs() {
        if (requests.size() == 0) return new ArrayList<>();

        return requests.stream()
                .map(this::toXBuffer)
                .collect(Collectors.toList());
    }

    @Override
    public void clearOutputs() {
        requests.clear();
    }


    private XBuffer toXBuffer(Request request) {
        XBuffer xBuffer = new XBuffer();
        xBuffer.cache(request.getContent());
        xBuffer.setxSocketId(request.getSocketId());
        return xBuffer;
    }
}
