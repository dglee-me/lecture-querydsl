package kr.co.dglee.lecture.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import kr.co.dglee.lecture.dto.MemberSearchCondition;
import kr.co.dglee.lecture.dto.MemberTeamDTO;
import kr.co.dglee.lecture.entity.Member;
import kr.co.dglee.lecture.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

  @Autowired
  EntityManager em;

  @Autowired
  MemberJpaRepository memberJpaRepository;

  @Test
  void basicTest() {
    Member member = new Member("member1", 10);
    memberJpaRepository.save(member);

    Member findMember = memberJpaRepository.findById(member.getId()).get();
    assertThat(findMember).isEqualTo(member);

    List<Member> result1 = memberJpaRepository.findAll();
    assertThat(result1).containsExactly(member);

    List<Member> result2 = memberJpaRepository.findAllByUsername(member.getUsername());
    assertThat(result2).containsExactly(member);
  }

  @Test
  void testBasicQueryDSL() {
    Member member = new Member("member1", 10);
    memberJpaRepository.save(member);

    Member findMember = memberJpaRepository.findById(member.getId()).get();
    assertThat(findMember).isEqualTo(member);

    List<Member> result1 = memberJpaRepository.findAllByQueryDSL();
    assertThat(result1).containsExactly(member);

    List<Member> result2 = memberJpaRepository.findAllByUsernameByQueryDSL(member.getUsername());
    assertThat(result2).containsExactly(member);
  }

  @Test
  void searchTest() {
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

    MemberSearchCondition condition = new MemberSearchCondition();
    condition.setAgeGoe(35);
    condition.setAgeLoe(40);
    condition.setTeamName("teamB");

    List<MemberTeamDTO> result = memberJpaRepository.searchByBuilder(condition);
    assertThat(result).extracting("username").containsExactly("member4");
  }

  @Test
  void searchTest2() {
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

    MemberSearchCondition condition = new MemberSearchCondition();
    condition.setAgeGoe(35);
    condition.setAgeLoe(40);
    condition.setTeamName("teamB");

    List<MemberTeamDTO> result = memberJpaRepository.search(condition);
    assertThat(result).extracting("username").containsExactly("member4");
  }
}