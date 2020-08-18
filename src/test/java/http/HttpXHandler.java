package http;

import codec.protocol.http.DefaultHttpXReader;
import codec.protocol.http.Request;
import core.XBuffer;
import core.XSocket;
import ext.AbstractXHandler;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2020/8/13 16:46
 **/
public class HttpXHandler extends AbstractXHandler {

    String httpResponse = "HTTP/1.1 200 OK\r\n" +
            "Content-Length: 38\r\n" +
            "Content-Type: text/html\r\n" +
            "\r\n" +
            "<html><body>greeting from SSNIO server</body></html>";

    @Override
    public List<XBuffer> doHandler(XSocket xSocket) {
        DefaultHttpXReader httpReader = (DefaultHttpXReader) xSocket.getXReader();

        List<Request> requests = httpReader
                .getRequests();

        System.err.println(requests.size() + " requests received from one NIO reading");
        requests.forEach(this::printRequest);

        List<XBuffer> xBufferList = requests.stream()
                .map(this::resp)
                .collect(Collectors.toList());

        finishHandling();

        return xBufferList;
    }


    private XBuffer resp(Request request) {
        byte[] resp_bytes = null;
        try {
            resp_bytes = httpResponse.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        XBuffer resp_buffer = new XBuffer();
        resp_buffer.setXSocketId(request.getSocketId());
        resp_buffer.cache(resp_bytes);
        return resp_buffer;
    }

    private void printRequest(Request request) {
        try {
            System.err.println(new String(request.getContent(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}
