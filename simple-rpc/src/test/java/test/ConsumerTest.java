package test;

import com.github.dragonhht.rpc.api.HelloService;
import com.github.dragonhht.rpc.common.CommonConstants;
import com.github.dragonhht.rpc.consumer.ProxyHandler;
import org.junit.Test;

/**
 * .
 *
 * @author: huang
 * @Date: 2019-5-31
 */
public class ConsumerTest {

    @Test
    public void testCall() {
        HelloService service = (HelloService) ProxyHandler.newInstance(null, CommonConstants.PROVIDER_PORT,
                CommonConstants.HOST, CommonConstants.REGISTRY_PORT, HelloService.class);
        System.out.println(service.sayHello("drag"));
    }

}
