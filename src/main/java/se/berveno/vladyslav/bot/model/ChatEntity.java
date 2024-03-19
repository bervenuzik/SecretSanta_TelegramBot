package se.berveno.vladyslav.bot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Chats")
@Data
@NoArgsConstructor
public class ChatEntity {

    @Id
    @Column(name = "chat_id" , unique = true, nullable = false)
    private Long chatId;

    @Column(name = "chat_name")
    private String chatName;
    @Column(name="is_started" , nullable = false)
    private Boolean started;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "chat_members",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private Set<Member> members;

    public ChatEntity(Long chatId , String chatName) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.started = false;
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

    public boolean isMemberJoined(Member member){
        return  members.contains(member);
    }


}
