package se.berveno.vladyslav.bot.Exeptions;

public class FailedRegistrationExeption extends TelegramBotCustomExeption{
    public FailedRegistrationExeption(String messege) {
        super(messege);
    }
}
