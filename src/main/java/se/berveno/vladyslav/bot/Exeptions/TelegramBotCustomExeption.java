package se.berveno.vladyslav.bot.Exeptions;

public class TelegramBotCustomExeption extends RuntimeException{

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public TelegramBotCustomExeption(String messege) {
        super(messege);
    }
}
