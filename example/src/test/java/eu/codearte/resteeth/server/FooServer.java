package eu.codearte.resteeth.server;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FooServer {

    @RequestMapping(value = "/echo/{message}", method = RequestMethod.GET)
    public String echo(String message) {
        return message + " " + message;
    }
}
