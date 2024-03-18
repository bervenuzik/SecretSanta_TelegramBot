package se.berveno.vladyslav.bot.model;

public enum MyBotCommands {
    HELP    ("help" , "This command will give you a manual for using this bot"),
    REGISTRATION("registrate" , "Use this command in chat with bot to registrate you in bot system"),
    WISH("wish_" ,"Use this command in chat with bot to add wishes, so your secret santa will now your preferences"),
    REGISTRATE_CHAT("registrate_chat","Use this command in your group-chat to registrate your chat in bot system"),
    JOIN("join" ,"Use this command in your group-chat to join Secret Santa game in your chat"),
    UNJOIN("unjoin","Use this command in your group-chat to unjoin Secret Santa game in your chat. !!!You cant use this command after start"),
    SHOW_ALL("show_all" , "Use this command in your group-chat to show all participants"),
    START_SECRET_SANTA("start_secret_santa","Use this commant in your group-chat to start game if every who wanted have joined a game"),
    RESET("reset" , "Use this command in your group-chat to to reset all participants of game"),
    UNREGISTRATE_CHAT("unregisterate_chat","Use this command in your group-chat to delete this chat from bot system. It is not enough just to delete a bot from chat"),
    UNREGISTRATE_USER("unregistrate_me", "Use this command to delete your self from all games and from bot's system");

    private final String  command;
    private final String  description;

    MyBotCommands(String  command , String description) {
        this.command = "/" + command;
        this.description =  description;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }
}
