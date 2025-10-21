package ch.brogli.graalvmperformance.sender;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "pingClient")
public interface PingClient {

    @RequestMapping(method = RequestMethod.GET, value = "/ping")
    String getPing();
}
