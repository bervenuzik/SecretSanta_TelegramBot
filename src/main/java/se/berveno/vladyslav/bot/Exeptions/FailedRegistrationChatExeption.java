package se.berveno.vladyslav.bot.Exeptions;

public class FailedRegistrationChatExeption  extends TelegramBotCustomExeption{
    public FailedRegistrationChatExeption(String messege) {
        super(messege);
    }
}
