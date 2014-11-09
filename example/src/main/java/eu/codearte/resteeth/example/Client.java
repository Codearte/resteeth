package eu.codearte.resteeth.example;

import eu.codearte.resteeth.annotation.RestClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestClient // FIXME: Should be used on field, not interface
interface Client {

    @RequestMapping(value = "/echo/{message}", method = RequestMethod.GET)
    String echo(@PathVariable("message") String message);
}