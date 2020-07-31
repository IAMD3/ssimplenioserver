package core;

import codec.CodeCFactory;
import codec.XReader;
import codec.XWriter;
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

    /**
     * common reader & writer -> should be adaptable for various protocols
     **/
    private XReader xReader;

    private XWriter xWriter;

    private boolean init;

    public XSocket(SocketChannel sc) {
        this.sc = sc;

        this.xSocketId = Config.getXSocketId();
        this.readBuffer = new XBuffer();
        this.writeBuffer = new XBuffer();

        init = false;
    }

    public void initCodeC(CodeCFactory factory){
        this.xReader = factory.createXReader();
        this.xWriter =  factory.createXWriter();
    }

}
