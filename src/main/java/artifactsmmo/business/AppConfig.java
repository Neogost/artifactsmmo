package artifactsmmo.business;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    private static String token;
    private static String domain;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    public static String getDomain() {
        return domain;
    }

    public static String getToken() {
        return token;
    }

    @Value("${api.artifacts.domain}")
    public void setDomain( String domain) {
        AppConfig.domain = domain;
    }
    @Value("${api.artifacts.token}")
    public void setToken( String token) {
        AppConfig.token = token;
    }
}
