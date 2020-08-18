package codec.protocol.http;

import codec.CodeCFactory;
import codec.DefaultXWriter;
import codec.XParser;
import codec.XWriter;

/** todo
 * @Author: Yukai
 * Description: master T
 * create time: 2020/7/31 17:07
 **/
public class HttpCodeCFactory implements CodeCFactory {
    @Override
    public XParser createXReader() {
        return new DefaultHttpXParser();
    }

    @Override
    public XWriter createXWriter() {
        return new DefaultXWriter();
    }
}
