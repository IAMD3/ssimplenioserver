package codec.protocol.http;

import codec.XParser;
import core.XBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2020/8/4 16:47
 **/
public class DefaultHttpXParser implements XParser {

    private List<XBuffer> requests;

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

        return requests;
    }

}
