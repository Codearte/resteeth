package eu.codearte.resteeth.example.boot;

import eu.codearte.resteeth.annotation.RestClient;
import eu.codearte.resteeth.config.EnableResteeth;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {EchoClientTest.Application.class})
@WebAppConfiguration
@IntegrationTest
public class EchoClientTest {

    @Autowired
    @Lazy
    @RestClient(endpoints = "http://localhost:8080")
    private EchoClient echoClient;

    @Test
    public void shouldTalkWithClient() throws InterruptedException {
        assertThat(echoClient.echo("foo")).isEqualTo("foo");
    }

    @Configuration
    @ComponentScan("eu.codearte.resteeth.example.boot")
    @EnableResteeth // FIXME: This should be removed after test fix
    @EnableAutoConfiguration
    public static class Application {
    }

    interface EchoClient {
        @RequestMapping(value = "/echo/{message}", method = RequestMethod.GET)
        String echo(@PathVariable("message") String message);
    }

}
