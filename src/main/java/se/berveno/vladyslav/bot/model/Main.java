package se.berveno.vladyslav.bot.model;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class Main  implements CommandLineRunner {
    @Autowired
    BotConfig config;
    @Autowired
    SantaBot bot;
    @Override
    public void run(String... args) throws Exception {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        try{
            telegramBotsApi.registerBot(bot);
        }catch (TelegramApiException e ){
            e.printStackTrace();
        }

    }
}
