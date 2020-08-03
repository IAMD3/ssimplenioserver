package ext;

import core.XSocket;

/** pipeline 扩展点
 * <p>
 * date : 2020/8/3
 * time : 16:53
 * </p>
 *
 * @author Master T
 */
public interface XHandler {
    void handle(XSocket socket);

    XHandler next();
}
