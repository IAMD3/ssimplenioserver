package http;

import core.XBuffer;
import ext.XHandler;

import java.io.UnsupportedEncodingException;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2020/8/13 16:46
 **/
public class HttpXHandler implements XHandler {

    String httpResponse = "HTTP/1.1 200 OK\r\n" +
            "Content-Length: 38\r\n" +
            "Content-Type: text/html\r\n" +
            "\r\n" +
            "<html><body>greeting from SSNIO server</body></html>";

    @Override
    public XBuffer handle(XBuffer reqBuffer) {

        System.err.println(" requests received from one NIO reading");
        System.err.println(new String(reqBuffer.getContent()));
        XBuffer resp = resp(reqBuffer);

        return resp;
    }

    @Override
    public XHandler next() {
        return null;
    }


    private XBuffer resp(XBuffer reqBuffer) {
        byte[] resp_bytes = null;
        try {
            resp_bytes = httpResponse.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        XBuffer resp_buffer = new XBuffer();
        resp_buffer.setxSocketId(reqBuffer.getxSocketId());
        resp_buffer.cache(resp_bytes);
        return resp_buffer;
    }



}
