package codec.protocol.http;

import codec.XReader;
import core.XBuffer;
import lombok.Data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2020/8/4 16:47
 **/
@Data
public class DefaultHttpXReader implements XReader {

   private List<Request> requests;

   private XBuffer readerBuffer;

   public DefaultHttpXReader(){
        requests = new ArrayList<>();
        readerBuffer =  new XBuffer();
   }

    @Override
    public void read(ByteBuffer src) {
    }

    @Override
    public List<XBuffer> getProtocolSpecialisedBufferBlocks() {
        return null;
    }
}
