import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Yukai
 * Description: master T
 * create time: 2020/7/31 15:07
 **/
@Data
public class XBuffer {

    private final int EXPENDABLE = 0;

    private final int EXPENDING = 1;

    /**
     * internal content
     */
    private byte[] content;

    /**
     * logical length of internal content
     */
    private int length;

    /**
     * 0: ready for expending
     * 1: in the processing of expending
     */
    private AtomicInteger flag = new AtomicInteger(EXPENDABLE);



    public XBuffer() {
        content = new byte[Config.BUFFER_INITIAL_SIZE];
        length = 0;
    }

    /**
     * race safely
     */
    public void expend2Double() {
        try {
            if (flag.compareAndSet(EXPENDABLE, EXPENDING)) {
                byte[] desc = new byte[content.length * 2];
                System.arraycopy(content, 0, desc, 0, length);
                content = desc;
            }
        } finally {
            flag.set(EXPENDABLE);
        }

    }
}
