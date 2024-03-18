package se.berveno.vladyslav.bot.Exeptions;

public class EmptyChatExeption extends TelegramBotCustomExeption{
    public EmptyChatExeption(String messege) {
        super(messege);
    }
}
