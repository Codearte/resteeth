package eu.codearte.resteeth.example.boot.server;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EchoServer {
    @RequestMapping(value = "/echo/{message}", method = RequestMethod.GET)
    public String echo(@PathVariable("message") String message) {
        return message;
    }
}