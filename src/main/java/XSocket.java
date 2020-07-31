import lombok.Data;

import java.nio.channels.SocketChannel;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2020/7/31 14:54
 **/
@Data
public class XSocket {

    private String xSocketId;

    private SocketChannel sc;

    public XSocket(SocketChannel sc) {
        this.sc = sc;

        xSocketId = Config.getXSocketId();
    }

}
