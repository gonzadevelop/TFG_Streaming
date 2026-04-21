package tfg.KeySound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StreamingBackEndApplication {

    public static void main(String[] args) {
        System.setProperty("org.springframework.security.web.firewall.StrictHttpFirewall.maxParameterCount", "500");
        System.setProperty("org.apache.tomcat.util.http.Parameters.MAX_COUNT", "1000");

        SpringApplication.run(StreamingBackEndApplication.class, args);
    }
}
