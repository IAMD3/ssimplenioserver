package http;

import codec.protocol.http.Request;
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
            "Content-Length: %s\r\n" +
            "Content-Type: text/html\r\n" +
            "\r\n" +
            "%s";

    @Override
    public XBuffer handle(XBuffer reqBuffer) {

        System.err.println(" requests received from one NIO reading");
        System.err.println(new String(reqBuffer.getContent()));
        XBuffer resp = resp((Request) reqBuffer);

        return resp;
    }

    @Override
    public XHandler next() {
        return null;
    }


    private XBuffer resp(Request request) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("echo from server:");
        sb.append("\r\n ");
        sb.append("header:" + request.getHeaderStr());
        sb.append("\r\n ");
        sb.append("body:" + request.getBodyStr());
        sb.append("</body></html>");

        XBuffer resp = new XBuffer();
        resp.setxSocketId(request.getxSocketId());
        try {

            String rst_body_str = sb.toString();
            String rst_body_length = sb.toString().getBytes("UTF-8").length + "";
            String rst_str = String.format(httpResponse, rst_body_length, rst_body_str);

            resp.cache(rst_str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return resp;
    }


}
