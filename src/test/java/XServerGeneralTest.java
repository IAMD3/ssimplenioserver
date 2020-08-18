import core.XServer;
import http.HttpXHandler;

import java.io.IOException;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2020/8/13 17:25
 **/
public class XServerGeneralTest {

    public static void main(String[] args) throws IOException {
        XServer server = new XServer();

        server.setHandlerPipeline(new HttpXHandler());

        server.start();
    }
}
