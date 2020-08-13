package codec.protocol.http;

import lombok.Data;
import lombok.experimental.Accessors;

/** 大道至简 通讯皆为字节流 -> 何必转来转去徒增烦恼
 * @Author: Yukai
 * Description: master T
 * create time: 2020/7/31 16:23
 **/
@Data
@Accessors(chain = true)
public class Request {
    /**
     * associate to
     * @See core.XSocket
     */
    private String socketId;

    private int requestLineOffset;

    private int headersOffset;

    private int bodyOffset;

    private byte[] content;
}
