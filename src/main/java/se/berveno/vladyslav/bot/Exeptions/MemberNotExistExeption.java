package se.berveno.vladyslav.bot.Exeptions;

public class MemberNotExistExeption extends TelegramBotCustomExeption{

    public MemberNotExistExeption(String messege) {
        super(messege);
    }
}
