package se.berveno.vladyslav.bot.Exeptions;

public class MemberNotInPartyExeption extends TelegramBotCustomExeption{
    public MemberNotInPartyExeption(String messege) {
        super(messege);
    }
}
