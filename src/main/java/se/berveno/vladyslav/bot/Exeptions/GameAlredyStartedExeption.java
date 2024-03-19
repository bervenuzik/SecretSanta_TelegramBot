package se.berveno.vladyslav.bot.Exeptions;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class GameAlredyStartedExeption extends TelegramBotCustomExeption {
    public GameAlredyStartedExeption(String messege) {
        super(messege);
    }
}
