package kr.co.dglee.lecture.entity;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberTest {

  @Autowired
  EntityManager em;

  @Test
  void testEntity() {
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");

    em.persist(teamA);
    em.persist(teamB);

    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 20, teamA);
    Member member3 = new Member("member3", 30, teamB);
    Member member4 = new Member("member4", 40, teamB);

    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);

    // 초기화
    em.flush();
    em.clear();

    List<Member> members = em.createQuery(
            "SELECT m FROM Member m" +
                " JOIN FETCH m.team t", Member.class)
        .getResultList();

    members.forEach(m -> {
      System.out.println("member = " + m);
      System.out.println("-> member.team = " + m.getTeam());
    });

    Assertions.assertEquals(4, members.size());
  }
}