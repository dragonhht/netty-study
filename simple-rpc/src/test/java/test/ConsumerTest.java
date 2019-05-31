package test;

import com.github.dragonhht.rpc.api.HelloService;
import com.github.dragonhht.rpc.common.CommonConstants;
import com.github.dragonhht.rpc.consumer.ProxyHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Scanner;

/**
 * .
 *
 * @author: huang
 * @Date: 2019-5-31
 */
@Slf4j
public class ConsumerTest {

    @Test
    public void testCall() {
        HelloService service = (HelloService) ProxyHandler.newInstance(null, CommonConstants.PROVIDER_PORT,
                CommonConstants.HOST, CommonConstants.REGISTRY_PORT, HelloService.class);
        System.out.println(service.sayHello("world"));
    }

    public static void main(String[] args) {
        HelloService service = (HelloService) ProxyHandler.newInstance(null, CommonConstants.PROVIDER_PORT,
                CommonConstants.HOST, CommonConstants.REGISTRY_PORT, HelloService.class);
        Scanner scanner = new Scanner(System.in);
        System.out.println("---------------请输入---------------");
        while (scanner.hasNextLine()) {
            String str = scanner.nextLine();
            System.out.println(service.sayHello(str));
        }
    }

}
