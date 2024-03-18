package se.berveno.vladyslav.bot.services;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import se.berveno.vladyslav.bot.model.Member;
import se.berveno.vladyslav.bot.repositories.MemberRepo;

import java.util.Optional;

@Service
@NoArgsConstructor
@Data
public class MemberService {

    MemberRepo repo;

    @Autowired
    public MemberService(MemberRepo repo) {
        this.repo = repo;
    }

    public Optional<Member> createNewMember(Member member){
            return Optional.of(repo.save(member));
    }


    public boolean createMember(Member member) {
        if(!repo.existsById(member.getId())) {
            repo.save(member);
            return true;
        }else{
            return false;
        }
    }

    public Optional<Member> getMemberById(Long id){
        return  repo.findById(id);
    }

    public boolean isMemberAlredyExist(Long userId) {
        return repo.existsById(userId);
    }
    public boolean saveMember(Member member){
        Member savedMember = repo.save(member);
        if(savedMember == null) return  false;
        return true;
    }
}
