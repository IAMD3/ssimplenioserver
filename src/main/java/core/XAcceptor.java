package core;

import codec.CodeCFactory;
import global.Config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 快速建立链接-> 包装链接对象 -> 放入队列
 *
 * @Author: Yukai
 * Description: master T
 * create time: 2020/7/31 14:44
 **/
public class XAcceptor implements Runnable {

    private final ServerSocketChannel ssc;
    private final CodeCFactory codeCFactory;

    public XAcceptor(CodeCFactory codeCFactory) throws IOException {
        this.codeCFactory = codeCFactory;

        this.ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(Config.PORT));
    }

    @Override
    public void run() {
        while (true) {
            try {
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);

                XSocket xSocket = new XSocket(sc);
                xSocket.initCodeC(this.codeCFactory);

                Config.INBOUND_QUEUE.offer(xSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
