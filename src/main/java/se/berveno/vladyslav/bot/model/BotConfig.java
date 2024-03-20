package se.berveno.vladyslav.bot.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.properties")
public class BotConfig {

    @Value(value = "${bot.name}")
    String botName;

    @Value(value = "${bot.token}")
    String tocken;
}
