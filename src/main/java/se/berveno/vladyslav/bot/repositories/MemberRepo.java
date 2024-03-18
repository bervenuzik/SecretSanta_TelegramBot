package se.berveno.vladyslav.bot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.berveno.vladyslav.bot.model.Member;
@Repository
public interface MemberRepo  extends JpaRepository<Member , Long> {

}
