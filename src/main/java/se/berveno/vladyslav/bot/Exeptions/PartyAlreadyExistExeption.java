package se.berveno.vladyslav.bot.Exeptions;

public class PartyAlreadyExistExeption  extends TelegramBotCustomExeption{
    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public PartyAlreadyExistExeption(String message) {
       super(message);
    }

}
