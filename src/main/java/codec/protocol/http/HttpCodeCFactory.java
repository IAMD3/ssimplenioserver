package codec.protocol.http;

import codec.CodeCFactory;
import codec.XReader;
import codec.XWriter;

/** todo
 * @Author: Yukai
 * Description: master T
 * create time: 2020/7/31 17:07
 **/
public class HttpCodeCFactory implements CodeCFactory {
    @Override
    public XReader createXReader(String socketId) {
        return new DefaultHttpXReader(socketId);
    }

    @Override
    public XWriter createXWriter() {
        return new DefaultHttpXWriter();
    }
}
