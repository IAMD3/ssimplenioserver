package codec;

/** @See codec.XReader
 *  @See codec.XWriter
 *  XReader / XWriter 不该被一个特定的协议限制
 *  使用抽象工厂方式生产这一组产品的实现
 * <p>
 * date : 2020/7/31
 * time : 17:04
 * </p>
 *
 * @author Master T
 */
public interface CodeCFactory {

    XReader createXReader();

    XWriter createXWriter();

}
