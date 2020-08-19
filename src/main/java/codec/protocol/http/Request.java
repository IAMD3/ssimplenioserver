package codec.protocol.http;

import com.sun.org.apache.regexp.internal.RE;
import core.XBuffer;

import java.io.UnsupportedEncodingException;

/**
 * 大道至简 通讯皆为字节流 -> 何必转来转去徒增烦恼
 *
 * @Author: Yukai
 * Description: master T
 * create time: 2020/7/31 16:23
 **/
public class Request extends XBuffer {

    private int headersOffset;

    private int bodyOffset;

    public Request() {
    }

    public String getHeaderStr() {
        int headerLength;
        if(bodyOffset == -1){
            headerLength = getLength() - headersOffset;
        }else{
            headerLength = bodyOffset - headersOffset;
        }

        return tryGetStr(headersOffset,headerLength,"UTF-8");
    }

    public String getBodyStr()  {
        if(bodyOffset == -1) return "";

        int bodyLength = getLength() - bodyOffset;

        return tryGetStr(bodyOffset,bodyLength,"UTF-8");
    }

    private String tryGetStr(int offset, int length, String charset) {
        if (length <= 0) return "";
        byte[] bytes_buffer = new byte[length];
        System.arraycopy(getContent(), offset, bytes_buffer, 0, length);

        String rst;
        try {
           rst = new String(bytes_buffer,charset);
        } catch (UnsupportedEncodingException e) {
            rst = "";
        }
        return rst;
    }

    public int getHeadersOffset() {
        return this.headersOffset;
    }

    public int getBodyOffset() {
        return this.bodyOffset;
    }

    public Request setHeadersOffset(int headersOffset) {
        this.headersOffset = headersOffset;
        return this;
    }

    public Request setBodyOffset(int bodyOffset) {
        this.bodyOffset = bodyOffset;
        return this;
    }


}
