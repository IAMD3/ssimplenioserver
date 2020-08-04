package core;

import codec.CodeCFactory;
import codec.protocol.http.HttpCodeCFactory;
import lombok.Setter;

import java.io.IOException;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2020/8/4 15:43
 **/
public class XServer {

    @Setter
    private CodeCFactory codeCFactory;


    public void start() throws IOException {
        if (codeCFactory == null) codeCFactory = new HttpCodeCFactory();

        XAcceptor xAcceptor = new XAcceptor(codeCFactory);
        XWorker xWorker = new XWorker();

        new Thread(xAcceptor)
                .start();

        new Thread(xWorker)
                .start();

    }
}
