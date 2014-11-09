package eu.codearte.resteeth.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, ClientTest.Config.class})
@WebAppConfiguration
@IntegrationTest
public class ClientTest {

    @Autowired
    Client client;

    @Test
    public void shouldTalkWithClient() {
        System.out.println(client.echo("foo"));

    }

    @Configuration
    @ComponentScan("eu.codearte.resteeth.server")
    static class Config {

    }


}
