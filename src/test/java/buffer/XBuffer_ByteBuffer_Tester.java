package buffer;

import core.XBuffer;
import global.Container;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/** ByteBuffer to XBuffer
 * @Author: Yukai
 * Description: master T
 * create time: 2020/8/4 17:09
 **/
public class XBuffer_ByteBuffer_Tester {

    public static void main(String[] args) throws UnsupportedEncodingException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Container.BYTE_BUFFER_INITIAL_SIZE);

        byte[] bytes = "helloWorld".getBytes("utf-8");
        byteBuffer.put(bytes);
        byteBuffer.flip();

        XBuffer xBuffer = new XBuffer();

        xBuffer.cache(byteBuffer);

        String str_need_trim = new String(xBuffer.getContent(), "utf-8");
        System.out.println(str_need_trim + ":" + xBuffer.getContent().length);

        xBuffer.trim(0, bytes.length);
        String str_trimmed = new String(xBuffer.getContent(), "utf-8");
        System.out.println(str_trimmed + ":" + xBuffer.getContent().length);

    }
}
