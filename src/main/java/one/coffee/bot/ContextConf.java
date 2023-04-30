package one.coffee.bot;

import chat.tamtam.botapi.client.TamTamClient;
import one.coffee.Main;
import one.coffee.sql.DB;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Configuration
@ComponentScan(
        basePackageClasses = {Main.class}
)
public class ContextConf {
    @Bean
    public static TamTamClient getTamTamClient(Properties properties) {
        return TamTamClient.create(properties.getProperty("api.key"));
    }

    @Bean
    public static DB getDB(Properties properties) {
        return new DB(properties);
    }

    @Bean
    @Primary
    public static Properties getProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/configuration.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Inside resources folder create configuration.properties");
        }
        return properties;
    }
}
