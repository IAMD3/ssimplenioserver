package codec.protocol.http;

import lombok.Data;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2020/7/31 16:23
 **/
@Data
public class Request {
    private String socketId;

    private byte[] content;

    private int requestLineOffset;

    private int headersOffset;

    private int bodyOffset;
}
