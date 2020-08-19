package codec.protocol.http;

import core.XBuffer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private Map<String, String> uriParamsMap;

    public Request() {
    }


    public Map<String, String> tryGetUriParams() {
        try {
            if (uriParamsMap != null) return uriParamsMap;

            uriParamsMap = new HashMap<>();
            String requestLine = tryGetStr(0, headersOffset, "utf-8");
            requestLine = URLDecoder.decode(requestLine);

            String[] pieces = requestLine.split("\\?");

            if (pieces.length != 2) return new HashMap<>();

            String paramSetStr = pieces[1];

            String paramReg = "\\w+=\\w+";
            Pattern pattern = Pattern.compile(paramReg);
            Matcher matcher = pattern.matcher(paramSetStr);

            while (matcher.find()) {
                String param_pair_str = matcher.group();
                String[] key_value_arr = param_pair_str.split("=");
                uriParamsMap.put(key_value_arr[0], key_value_arr[1]);
            }

            return uriParamsMap;
        } catch (Exception e) {
            return new HashMap<>();
        }

    }

    public String getHeaderStr() {
        int headerLength;
        if (bodyOffset == -1) {
            headerLength = getLength() - headersOffset;
        } else {
            headerLength = bodyOffset - headersOffset;
        }

        return tryGetStr(headersOffset, headerLength, "UTF-8");
    }

    public String getBodyStr() {
        if (bodyOffset == -1) return "";

        int bodyLength = getLength() - bodyOffset;

        return tryGetStr(bodyOffset, bodyLength, "UTF-8");
    }

    private String tryGetStr(int offset, int length, String charset) {
        if (length <= 0) return "";
        byte[] bytes_buffer = new byte[length];
        System.arraycopy(getContent(), offset, bytes_buffer, 0, length);

        String rst;
        try {
            rst = new String(bytes_buffer, charset);
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
