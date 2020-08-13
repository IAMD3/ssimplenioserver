package http;

import codec.protocol.http.HttpUtil;
import codec.protocol.http.Request;
import com.sun.tools.javac.util.Assert;
import core.XBuffer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2020/8/4 21:30
 **/
public class HttpUtilTest {
    private static final String HTTP_REQUEST = "GET / HTTP/1.1\r\n" +
            "Content-Length: 5\r\n" +
            "yk_header: cool\r\n" +
            "\r\nABCDE";


    public static void main(String[] args) throws IOException {
        //模拟一次完整http报文反序列化
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 4);
        byteBuffer.put(HTTP_REQUEST.getBytes("UTF-8"));
        byteBuffer.flip();

        XBuffer xBuffer = new XBuffer();
        //put a HTTP Request to XBuffer
        xBuffer.cache(byteBuffer);
        //parse to a HTTP Request
        Request request = HttpUtil.tryToParseHttpRequest(xBuffer);
        Assert.checkNonNull(request);
        //XBuffer got reset after a successful parsing
        Assert.check(xBuffer.getLength() == 0);

        //模拟粘包/拆包场景
        String str_combine = HTTP_REQUEST + HTTP_REQUEST;// 59Bytes each xxD
        byte[] bytes_combine = str_combine.getBytes("UTF-8");
        byte[] bytes_part_1 = new byte[10];
        byte[] bytes_part_2 = new byte[bytes_combine.length - bytes_part_1.length];
        System.arraycopy(bytes_combine, 0, bytes_part_1, 0, bytes_part_1.length);//0-9
        System.arraycopy(bytes_combine, bytes_part_1.length, bytes_part_2, 0, bytes_part_2.length);
       //模拟第一次nio下读到不完整的业务数据包 -> 无法反序列化成一个http报文
        xBuffer.cache(bytes_part_1);
        Request request_non = HttpUtil.tryToParseHttpRequest(xBuffer);
        //cannot parse an incomplete request
        Assert.checkNull(request_non);
        //模拟第二次nio下读到上次不完整的业务数据包其他部分+一个完整的业务数据包场景..
        xBuffer.cache(bytes_part_2);
        //now we can decode two consequent HTTP Requests
        Request complete_request_1 = HttpUtil.tryToParseHttpRequest(xBuffer);
        Request complete_request_2 = HttpUtil.tryToParseHttpRequest(xBuffer);
        Assert.checkNonNull(complete_request_1);
        Assert.checkNonNull(complete_request_2);
    }
}
