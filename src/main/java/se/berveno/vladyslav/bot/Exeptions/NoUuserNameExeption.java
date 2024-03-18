package se.berveno.vladyslav.bot.Exeptions;

public class NoUuserNameExeption  extends TelegramBotCustomExeption{
    public NoUuserNameExeption(String messege) {
        super(messege);
    }
}
