package se.berveno.vladyslav.bot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Chats")
@Data
@NoArgsConstructor
public class ChatEntity {

    @Id
    @Column(name = "chat_id" , unique = true)
    private Long chatId;

    @Column(name = "chat_name")
    private String chatName;


    @ManyToMany(fetch = FetchType.EAGER ,cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "chat_members",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private Set<Member> members;

    public ChatEntity(Long chatId , String chatName) {
        this.chatId = chatId;
        this.chatName = chatName;
        members = new HashSet<>();
    }

    public void addMember(Member member){
        members.add(member);
    }
    public boolean deleteMember(Member memberToDelete){
        if(members.contains(memberToDelete)){
            members.remove(memberToDelete);
            return true;
        }
        return false;
    }

    public boolean isMemberJoinedAlready(Member member){
        return  members.contains(member);
    }

}
