package one.coffee;

import chat.tamtam.bot.exceptions.TamTamBotException;
import one.coffee.bot.ContextConf;
import one.coffee.bot.OneCoffeeBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = createContext();
        context.refresh();
        try {
            context.getBean(OneCoffeeBot.class).start();
        } catch (TamTamBotException e) {
            LOG.error("Failed to start bot: " + e.getMessage());
            System.exit(1);
        }
    }

    private static AnnotationConfigApplicationContext createContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("prod");
        context.register(ContextConf.class);
        return context;
    }
}
