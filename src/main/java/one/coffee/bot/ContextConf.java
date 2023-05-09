package one.coffee.bot;

import chat.tamtam.botapi.client.TamTamClient;
import one.coffee.Main;
import one.coffee.sql.DB;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("/application.properties")
@ComponentScan(
        basePackageClasses = {Main.class}
)
public class ContextConf {
    @Bean
    public static TamTamClient getTamTamClient(@Value("${api.key}") String API_KEY) {
        return TamTamClient.create(API_KEY);
    }

    @Bean
    public static DB getDB(@Value("${db.URL}") String DB_URL) {
        return new DB(DB_URL);
    }
}
