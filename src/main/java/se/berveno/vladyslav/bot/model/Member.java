package se.berveno.vladyslav.bot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "members")
@Data
@NoArgsConstructor
public class Member {
    @Id
    private Long id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "nick_name",nullable = false)
    private String nickName;


    @Column(name = "wishes",nullable = false, columnDefinition = "TEXT")
    private String wish;



    public Member(Long id, String name , String nickName) {
        this.id = id;
        this.name = name;
        this.nickName =nickName;
        this.wish = "";
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
