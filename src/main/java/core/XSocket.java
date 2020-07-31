package core;

import core.XBuffer;
import global.Config;
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

    private XBuffer readBuffer;

    private XBuffer writeBuffer;


    public XSocket(SocketChannel sc) {
        this.sc = sc;

        this.xSocketId = Config.getXSocketId();
        this.readBuffer = new XBuffer();
        this.writeBuffer = new XBuffer();

    }

}
