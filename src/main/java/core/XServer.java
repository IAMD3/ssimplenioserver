package core;

import codec.CodeCFactory;
import codec.protocol.http.HttpCodeCFactory;
import ext.XHandler;

import java.io.IOException;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2020/8/4 15:43
 **/
public class XServer {


    private CodeCFactory codeCFactory;

    private XHandler handlerPipeline;


    public void start() throws IOException {
        if (codeCFactory == null) codeCFactory = new HttpCodeCFactory();

        XAcceptor xAcceptor = new XAcceptor(codeCFactory);

        XWorker xWorker = new XWorker();
        xWorker.setHandler(handlerPipeline);

        new Thread(xAcceptor)
                .start();

        new Thread(xWorker)
                .start();
        System.err.println("welcome to SSNIO server");
    }


    public void setHandlerPipeline(XHandler handlerPipeline) {
        this.handlerPipeline = handlerPipeline;
    }

}
