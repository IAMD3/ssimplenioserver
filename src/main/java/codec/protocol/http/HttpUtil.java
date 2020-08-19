package codec.protocol.http;

import core.XBuffer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * target - > 解析首部段包含content-length(记录body长度) http报文
 * 取第一行 -> 作为请求行
 * 不断取后面行作为header行,并且判断有没有content-length
 * 取到连续两次\r\n(header与body之间有两行) 作为body
 *
 * @Author: Yukai
 * Description: master T
 * create time: 2020/8/4 20:12
 **/
public class HttpUtil {

    /**
     * must contained from a complete http request
     */
    private static final byte[] CONTENT_LENGTH = new byte[]{'C', 'o', 'n', 't', 'e', 'n', 't', '-', 'L', 'e', 'n', 'g', 't', 'h'};

    public static Request tryToParseHttpRequest(XBuffer readerBuffer) throws IOException {
        if (readerBuffer.getLength() == 0) {
            return null;
        }

        readerBuffer.trim(0, readerBuffer.getLength());
        byte[] content = readerBuffer.getContent();

        int request_line_end_index = findNextLine(0, content.length, content);
        if (request_line_end_index == -1) return null;

        int header_start_index = request_line_end_index + 1;
        int header_end_index = findNextLine(header_start_index, content.length, content);
        int content_length = 0;


        while (/**not qualified**/header_end_index != -1 && /**reach body**/header_end_index != header_start_index + 1) {
            if (matches(content, header_start_index, CONTENT_LENGTH)) {
                content_length = findContentLength
                        (content, header_start_index, header_end_index);
            }

            header_start_index = header_end_index + 1;
            header_end_index = findNextLine(header_start_index, content.length, content);
        }

        // "GET / HTTP/1.1\r\n
        //Head:aaa\r\n
        // \r\n";
        if (/**header not complete**/header_end_index == -1) return null;

        if (content_length == 0) {
            //return a request without body
            Request request = new Request();
            request.setxSocketId(readerBuffer.getxSocketId());
            request.setContent(content);
            request.setHeadersOffset(request_line_end_index + 1);
            request.setBodyOffset(-1);
            request.setLength(content.length);
            //clear all data of readerBuffer;
            readerBuffer.reset();
            return request;
        }

        //try to parse body
        int body_start_index = header_end_index + 1;
        int body_end_index = body_start_index + content_length;

        if (body_end_index == content.length) {
            // rare but perfect condition
            Request request = new Request();

            request.setxSocketId(readerBuffer.getxSocketId());
            request.setContent(content);
            request.setHeadersOffset(request_line_end_index + 1);
            request.setBodyOffset(body_start_index);
            request.setLength(content.length);

            //clear all data of readerBuffer;
            readerBuffer.reset();
            return request;
        } else if (body_end_index < content.length) {
            //拆包
            Request request = new Request();

            byte[] request_content = new byte[body_end_index];
            System.arraycopy(content, 0, request_content, 0, body_end_index);

            request.setxSocketId(readerBuffer.getxSocketId());
            request.setContent(request_content);
            request.setHeadersOffset(request_line_end_index + 1);
            request.setBodyOffset(body_start_index);
            request.setLength(content.length);
            // remain the rest part of buffer
            int offset = body_end_index + 1;
            readerBuffer.trim(offset, readerBuffer.getLength() - offset);
            return request;
        }

        return null;
    }


    private static int findContentLength(byte[] src, int startIndex, int endIndex) throws UnsupportedEncodingException {
        int indexOfColon = findNext(src, startIndex, endIndex, (byte) ':');

        //skip spaces after colon
        int index = indexOfColon + 1;
        while (src[index] == ' ') {
            index++;
        }

        int valueStartIndex = index;
        int valueEndIndex = index;
        boolean endOfValueFound = false;

        while (index < endIndex && !endOfValueFound) {
            switch (src[index]) {
                case '0':
                    ;
                case '1':
                    ;
                case '2':
                    ;
                case '3':
                    ;
                case '4':
                    ;
                case '5':
                    ;
                case '6':
                    ;
                case '7':
                    ;
                case '8':
                    ;
                case '9': {
                    index++;
                    break;
                }

                default: {
                    endOfValueFound = true;
                    valueEndIndex = index;
                }
            }
        }

        return Integer.parseInt(new String(src, valueStartIndex, valueEndIndex - valueStartIndex, "UTF-8"));

    }

    private static int findNext(byte[] src, int startIndex, int endIndex, byte value) {
        for (int index = startIndex; index < endIndex; index++) {
            if (src[index] == value) return index;
        }
        return -1;
    }


    private static boolean matches(byte[] src, int offset, byte[] value) {
        for (int i = offset, n = 0; n < value.length; i++, n++) {
            if (src[i] != value[n]) return false;
        }
        return true;
    }

    private static int findNextLine(int startIndex, int endIndex, byte[] src) {
        for (int index = startIndex; index < endIndex; index++) {
            if (src[index] == '\n') {
                if (src[index - 1] == '\r') {
                    return index;
                }
            }
            ;
        }
        return -1;
    }
}
