package se.berveno.vladyslav.bot.services;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.berveno.vladyslav.bot.model.ChatEntity;
import se.berveno.vladyslav.bot.repositories.ChatRepo;

import java.util.Optional;

@Service
@NoArgsConstructor
@Data
public class ChatService {

    ChatRepo repo;
    @Autowired
    public ChatService(ChatRepo repo) {
        this.repo = repo;
    }

    public ChatEntity registrateChat(ChatEntity chat){
           return repo.save(chat);
    }


    public Optional<ChatEntity> findPartyByChatId(Long id){
        return repo.findById(id);
    }

    public ChatEntity saveChat(ChatEntity chatParty) {
           return  repo.save(chatParty);
    }
    public boolean isPartyExist(Long id){
        return repo.existsById(id);
    }

    public  void deleteChat(ChatEntity chat){
        repo.deleteById(chat.getChatId());
    }

//    public  void addMember(Member member){
//    }
}
