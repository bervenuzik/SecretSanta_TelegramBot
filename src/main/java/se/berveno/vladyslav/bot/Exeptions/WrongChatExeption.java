package se.berveno.vladyslav.bot.Exeptions;

public class WrongChatExeption  extends TelegramBotCustomExeption{
    public WrongChatExeption(String messege) {
        super(messege);
    }
}
