package codec.protocol.http;

/** 大道至简 通讯皆为字节流 -> 何必转来转去徒增烦恼
 * @Author: Yukai
 * Description: master T
 * create time: 2020/7/31 16:23
 **/
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

    public Request() {
    }

    public String getSocketId() {
        return this.socketId;
    }

    public int getRequestLineOffset() {
        return this.requestLineOffset;
    }

    public int getHeadersOffset() {
        return this.headersOffset;
    }

    public int getBodyOffset() {
        return this.bodyOffset;
    }

    public byte[] getContent() {
        return this.content;
    }

    public Request setSocketId(String socketId) {
        this.socketId = socketId;
        return this;
    }

    public Request setRequestLineOffset(int requestLineOffset) {
        this.requestLineOffset = requestLineOffset;
        return this;
    }

    public Request setHeadersOffset(int headersOffset) {
        this.headersOffset = headersOffset;
        return this;
    }

    public Request setBodyOffset(int bodyOffset) {
        this.bodyOffset = bodyOffset;
        return this;
    }

    public Request setContent(byte[] content) {
        this.content = content;
        return this;
    }



}
