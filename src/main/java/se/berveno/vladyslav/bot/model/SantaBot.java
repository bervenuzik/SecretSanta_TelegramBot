package se.berveno.vladyslav.bot.model;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import se.berveno.vladyslav.bot.Exeptions.*;
import se.berveno.vladyslav.bot.services.MemberService;
import se.berveno.vladyslav.bot.services.ChatService;


import java.util.*;

@Component
@NoArgsConstructor
@Slf4j
public class SantaBot  extends TelegramLongPollingBot{

    private BotConfig config;

    private ChatService chatService;

    private MemberService memberService;



    private SendMessage sender;
    private final String HELP_MESSAGE =  "This bot is developed for friend.\nIt is fully free\nYou can create only one party, but you can participate in many";
    private final String WRONG_INPUT_MESSAGE =  "Sorry , this command is not supported";

    @Autowired
    public SantaBot(BotConfig config, ChatService chatService , MemberService memberService)  {
        super(config.getTocken());
        this.config = config;
        this.chatService = chatService;
        this.memberService = memberService;
        sender = new SendMessage();
        List<BotCommand> botCommands = createBotCommands();
        try {
            this.execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(),null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot commands " + e.getMessage()+ " in  ===> " + e.getClass());
        }

    }
    private List<BotCommand> createBotCommands(){
        List<BotCommand> botCommands = new ArrayList<>();
        List<MyBotCommands> commands = List.of(MyBotCommands.class.getEnumConstants());
        for (MyBotCommands command : commands) {
            botCommands.add(new BotCommand(command.getCommand(),command.getDescription()));
        }
        return botCommands;
    }
    @Override
    public String getBotUsername() {
        return "secret_santaFP_bot";
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
                new Thread(()->{
                    Update deepCopy = SerializationUtils.clone(update);
                    String command;
                    Chat chat = update.getMessage().getChat();
                    command = update.getMessage().getText();
                    if (chat.isSuperGroupChat()) {
                        if(command.endsWith("@"+getBotUsername())){
                            command = command.substring(0 , command.length() - (getBotUsername().length()+1));
                            action(command,deepCopy);
                        }else {
                            return;
                        }
                    }
                    if(chat.isUserChat()){
                        action(command , deepCopy);
                    }
                }).run();
        }
    }


        private void action(String command , Update update){
            try {
                List<MyBotCommands> botCommands = List.of(MyBotCommands.class.getEnumConstants());
                Optional<MyBotCommands> userChoise = Optional.empty();
                for (MyBotCommands cmd : botCommands) {
                    if(command.equals(cmd.getCommand()))  userChoise = Optional.of(cmd);
                }

                if(userChoise.isPresent()){
                    switch (userChoise.get()){
                        case HELP               ->  {showHelp(update);return;}
                        case DELETE_USER        ->  {deleteUser(update);return;}
                        case WISH               ->  {addWish(update);return;}
                        case REGISTRATE_CHAT    ->  {registrateChat(update);return;}
                        case JOIN               ->  {joinParty(update);return;}
                        case SHOW_ALL           ->  {showAllParticipants(update);return;}
                        case UNJOIN             ->  {leftParty(update);return;}
                        case RESET              ->  {resetMembers(update);return;}
                        case START_SECRET_SANTA ->  {startGame(update);return;}
                        case UNREGISTRATE_CHAT  ->  {unregistrateChat(update);return;}
                    }
                }
                if (command.startsWith(MyBotCommands.WISH.getCommand())) {
                    addWish(update);
                    return;
                }
                sendMessage(WRONG_INPUT_MESSAGE ,update);
            }catch (TelegramBotCustomExeption exp){
                log.error(exp.getMessage() + exp.getStackTrace());
                sendMessage(exp.getMessage(),update);
            }

        }
        @Transactional(noRollbackFor = TelegramApiException.class)
        public void deleteUser(Update update) {
        if(!isPrivateChat(update)){
            throw new WrongChatExeption("You should add wishes in private chat with this bot");
        }
        Long memberID = getMemberId(update);
        Optional<Member> memberOpt = memberService.getMemberById(memberID);
        if(memberOpt.isEmpty()){
            log.warn("user is not registred but tries to delete itself");
            throw new MemberNotExistExeption("You didn't joined any party yet, so you are not registred yet");
        }
        Member member = memberOpt.get();
            for (ChatEntity chat: member.getChats()) {
                chat.deleteMember(member);
                chatService.saveChat(chat);
            }
            memberService.delete(member);
        sendMessage("You are successfully deleted your self" ,update);
    }

    //NOT TRANSACTIONAL
    private void startGame(Update update){
            Chat chat  = update.getMessage().getChat();
            if(!chat.isSuperGroupChat()){
                throw new WrongChatExeption("You can use this chat only in Group Chats");
            }
            Long chatID = getChatId(update);
            Optional<ChatEntity> chatEntityOptional = chatService.findPartyByChatId(chatID);

            if(chatEntityOptional.isEmpty()){
                log.warn("Chat tried to start a game in not registred chat : " + chat.toString());
                throw new ChatIsNotExistExeption("You didn't registrate this chat yet");
            }
            ChatEntity chatEntity = chatEntityOptional.get();
            if(chatEntity.getStarted()){
                throw new GameAlredyStartedExeption("Game already started , reset tha game first.");
            }
            if(chatEntity.getMembers().size() < 3){
                log.warn("Chat tried to start a game without particapants");
                throw  new EmptyChatExeption("You have to add at least 3 participants to start");
            }
            chatEntity.setStarted(true);
            Set<Member> membersSet = chatEntity.getMembers();
            chatService.saveChat(chatEntity);
            List<Member> membersList= new ArrayList<>(membersSet);
            Collections.shuffle(membersList);
            notifyMembers(membersList , update);
            sendMessage("your game is successfully started! Have fun =)",update);
        }

        private void notifyMembers(List<Member> membersList , Update update){
            Member firstMember = membersList.get(0);
            Member lastMember = membersList.get(membersList.size()-1);
            Member member1;
            Member member2;

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < membersList.size(); i++) {
                if(i == membersList.size()-1){
                    member1 = lastMember;
                    member2 = firstMember;
                } else {
                    member1 = membersList.get(i);
                    member2 = membersList.get(i+1);
                }
                stringBuilder.append("You have to make a present for @");
                stringBuilder.append(member2.getNickName());
                stringBuilder.append("\n");
                if(member2.getWish().length() > 0){
                    stringBuilder.append("here is his/her wish: " + member2.getWish());
                }else {
                    stringBuilder.append("He/she didn't write a wish");
                }
                sendMessage(stringBuilder.toString(),update ,member1.getId() );
                stringBuilder.setLength(0);
            }
        }

        //NOT TRANSACTIONAL
        private void  showAllParticipants(Update update){
            Chat chat  = update.getMessage().getChat();
            if(!chat.isSuperGroupChat()){
                throw new WrongChatExeption("You can use this chat only in Group Chats");
            }
            Long chatID = getChatId(update);
            Optional<ChatEntity> chatEntityOptional = chatService.findPartyByChatId(chatID);
            if(chatEntityOptional.isPresent()){
                StringBuilder stringBuilder = new StringBuilder();
                ChatEntity chatEntity = chatEntityOptional.get();
                Set<Member> members = chatEntity.getMembers();
                if(members.isEmpty()) {
                    sendMessage("No one have joined a game yet",update);
                    return;
                }
                List<Member> membersList = new ArrayList<>(members);
                stringBuilder.append("Here is all participants for now: \nTotal: " + members.size() + " person(s) \n");

                for (int i = 0; i < membersList.size(); i++) {
                    stringBuilder.append(i+1 +". @" + membersList.get(i).getNickName() + " \n");
                }
                sendMessage(stringBuilder.toString(),update);
            }

        }

        public void resetMembers(Update update){
            Chat chat  = update.getMessage().getChat();
            if(!chat.isSuperGroupChat()){
                throw new WrongChatExeption("You can use this chat only in Group Chats");
            }
            Long chatID = getChatId(update);
            Optional<ChatEntity> chatEntityOptional = chatService.findPartyByChatId(chatID);
            if(chatEntityOptional.isEmpty()){
                log.warn("Chat tried to reset members in not registred chat : " + chat.toString());
                throw new ChatIsNotExistExeption("You didn't registrate this chat yet");
            }
            ChatEntity chatEntity = chatEntityOptional.get();
            if(chatEntity.getMembers().isEmpty()){
                log.warn("Chat tried to reset empty chat");
                throw  new EmptyChatExeption("This chat have no participants yet");
            }
            chatEntity.setMembers(new HashSet<>());
            chatEntity.setStarted(false);
            chatService.saveChat(chatEntity);
            sendMessage("chat is successfully reset", update);
        }

        @Transactional
        public void leftParty(Update update) {
        Chat chat = update.getMessage().getChat();
        if(!chat.isSuperGroupChat()){
            throw new WrongChatExeption("You can use this chat only in Group Chats");
        }
        Long chatID = getChatId(update);
        Long memberId = getMemberId(update);
        String memberNickName = getUsersNickName(update);
        Optional<Member> member = memberService.getMemberById(memberId);
        if(member.isEmpty()){
            log.warn("User is trying to left game , but he is not registred "+ member.toString());
            throw new MemberNotExistExeption("You didn't registrate yourself @" + memberNickName + " yet.");
        }
        Optional<ChatEntity> chatEntityOptional = chatService.findPartyByChatId(chatID);
        if(chatEntityOptional.isEmpty()){
            log.warn("User is trying to left game , but chat is  not registred "+ member.toString() + "  chat: " + chatEntityOptional.toString());
            throw new ChatIsNotExistExeption("This chat is not registred yet.");
        }
        ChatEntity chatEntity = chatEntityOptional.get();
        Member memberEntity = member.get();

            if(chatEntity.deleteMember(memberEntity)){
                chatService.saveChat(chatEntity);
                sendMessage("@"+ memberNickName + " You have left a Secret Santa game",update);
            }else {
                throw new MemberNotInPartyExeption("You have not joined yet");
            }


    }

    @Transactional
    public void unregistrateChat(Update update) {
        Chat chat  = update.getMessage().getChat();
        if(!chat.isSuperGroupChat()){
            throw new WrongChatExeption("You can use this chat only in Group Chats");
        }
        Long chatID = getChatId(update);
        Optional<ChatEntity> chatEntityOptional = chatService.findPartyByChatId(chatID);
        if(chatEntityOptional.isEmpty()){
            log.warn("Chat tried to unregistrate chat that is not registred yet : " + chat.toString());
            throw new ChatIsNotExistExeption("You didn't registrate this chat yet");
        }
        ChatEntity chatEntity = chatEntityOptional.get();
        chatEntity.setMembers(new HashSet<>());
        chatEntity = chatService.saveChat(chatEntity);
        chatService.deleteChat(chatEntity);
        sendMessage("chat is successfully unregistred", update);

    }



    private void showHelp(Update update){
        sendMessage(HELP_MESSAGE,update);
    }

    private void sendMessage(String message , Update update){
        sender.setChatId(update.getMessage().getChatId().toString());
        sender.setText(message);
        try {
            execute(sender); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void sendMessage(String message , Update update , Long chatId){
        sender.setChatId(chatId);
        sender.setText(message);
        try {
            execute(sender); // Call method to send the message
            sender.setChatId(update.getMessage().getChatId().toString());
        } catch (TelegramApiException e) {
            e.printStackTrace();
            sender.setChatId(update.getMessage().getChatId().toString());
        }
    }

    @Transactional
    public void registrateChat(Update update){
        Chat chat = update.getMessage().getChat();
        if(!chat.isSuperGroupChat()){
            log.warn("User: @"+ getUsersNickName(update)+" is trying to registrate not a group chat.");
            throw new WrongChatExeption("You can use this chat only in Group Chats");
        }
        Long chatID = getChatId(update);
        String name = update.getMessage().getChat().getTitle();

        ChatEntity chatEntity = new ChatEntity(chatID , name);
        boolean isChatAlredyRegistrated = chatService.isPartyExist(chatID);
        chatEntity = chatService.registrateChat(chatEntity);
        if(isChatAlredyRegistrated){
            log.info("Updated info about chat "+ chatEntity.toString());
            sendMessage("You have successfully updated info about your chat.", update);
        }else{
            log.info("registrated a new chat "+ chatEntity.toString());
            sendMessage("You have successfully registered your chat.", update);
        }
    }
    @Transactional
    public void joinParty(Update update) {
        Chat chat = update.getMessage().getChat();
        if(!chat.isSuperGroupChat()){
            log.warn("User: @"+ getUsersNickName(update)+" is trying to join not in a group chat.");
            throw new WrongChatExeption("You can use this command only in Group Chats");
        }
        Member member = createMember(update);
        Long chatId = getChatId(update);
        Optional<ChatEntity> ChatEntityOpt  = chatService.findPartyByChatId(chatId);
        String memberNickName = member.getNickName();
        if(ChatEntityOpt.isPresent()){
            ChatEntity chatEntity = ChatEntityOpt.get();
            Long userId = member.getId();
            String nickName = member.getNickName();
            String firstName = update.getMessage().getFrom().getFirstName();
                if(chatEntity.isMemberJoined(member)){
                    sendMessage("@"+ memberNickName + " You have already joined Secret Santa Game!", update);
                    return;
                }
                chatEntity.addMember(member);
                chatService.saveChat(chatEntity);
                sendMessage("@"+ memberNickName + " have joined Secret Santa Game!", update);
        }else {
            throw  new ChatIsNotExistExeption("You didn't registrate this chat yet");
        }
    }


    private Member createMember(Update update){
        Long userId = getMemberId(update);
        String nickName = getUsersNickName(update);
        if(nickName == null){
            throw new NoUuserNameExeption("Opsss, you don't have a user name. Create it first , then try again");
        }
        String firstName = update.getMessage().getFrom().getFirstName();
        Optional<Member> member = memberService.saveMember(new Member(userId, firstName, nickName));
        if(member.isPresent()){
            return member.get();
        }else {
            log.error("Failed registration for user => " + member.toString());
            throw new FailedRegistrationExeption("Opsss, something went wrong. Try again");
        }
    }

    private void addWish(Update update){
        if(!isPrivateChat(update)){
            throw new WrongChatExeption("You should add wishes in private chat with this bot");
        }
        Long memberId = getMemberId(update);
        Optional <Member> member = memberService.getMemberById(memberId);
        if(member.isEmpty()){
            throw new MemberNotExistExeption("You have to registrate yourself first");
        }

        String message = update.getMessage().getText().toString();
        String wish = getWishFromMessage(message);

        member.get().setWish(wish);
        if(memberService.saveMember(member.get()).isPresent()){
            sendMessage("You have added a wish => '" + wish + " '", update);
        }else {
            log.error("Failed adding of wish to user => " + member.get().toString());
            throw new AddWishExeption("Adding of wish is failed , try again");
        }

    }

    private String getWishFromMessage(String message) {
        return  message.substring(MyBotCommands.WISH.getCommand().length());
    }

    private boolean isPrivateChat(Update update){
        return update.getMessage().getChat().isUserChat();
    }


    private Long getChatId(Update update){
        return update.getMessage().getChatId();
    }
    private Long getMemberId(Update update){
        return  update.getMessage().getFrom().getId();
    }
    private String getUsersNickName(Update update){
        return  update.getMessage().getFrom().getUserName();
    }

}
