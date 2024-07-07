package kr.co.dglee.lecture;

import static kr.co.dglee.lecture.entity.QMember.member;
import static kr.co.dglee.lecture.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import kr.co.dglee.lecture.entity.Member;
import kr.co.dglee.lecture.entity.Team;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

  @Autowired
  EntityManager em;

  JPAQueryFactory queryFactory;

  @BeforeEach
  public void before() {
    queryFactory = new JPAQueryFactory(em);

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
  }

  @DisplayName("JPQL로 JPQL 실행")
  @Test
  public void startJPQL() {

    // Member1을 찾는 법
    Member findByJPQL = em.createQuery("SELECT m FROM Member m "
            + "WHERE m.username = :username", Member.class)
        .setParameter("username", "member1")
        .getSingleResult();

    Assertions.assertEquals("member1", findByJPQL.getUsername());
  }

  @DisplayName("Querydsl로 JPQL 대체")
  @Test
  public void startQuerydsl() {
    // Member1을 찾는 법 (단건 조회)

    Member findMember = queryFactory
        .select(member)
        .from(member)
        .where(member.username.eq("member1")
            .and(member.age.eq(10)))
        .fetchOne();

    Assertions.assertEquals("member1", findMember.getUsername());
  }

  @Test
  public void resultFetch() {
    List<Member> fetch = queryFactory
        .selectFrom(member)
        .fetch();

    Member fetchOne = queryFactory
        .selectFrom(member)
        .where(member.username.eq("member1"))
        .fetchOne();

    Member fetchFirst = queryFactory
        .selectFrom(member)
        .fetchFirst();

    QueryResults queryResults = queryFactory
        .selectFrom(member)
        .fetchResults();

    System.out.println("fetchResults.getTotal() = " + queryResults.getTotal());
    List<Member> content = queryResults.getResults();
  }

  /**
   * 회원 정렬 순서 1. 회원 나이 내림차순 (desc) 2. 회원 이름 올림차순 (asc) 단 2에서 회원 이름이 없으면 마지막에 출력 (nulls last)
   */
  @Test
  public void sort() {
    em.persist(new Member(null, 100));
    em.persist(new Member("member5", 100));
    em.persist(new Member("member6", 100));

    List<Member> result1 = queryFactory
        .selectFrom(member)
        .where(member.age.eq(100))
        .orderBy(member.age.desc(), member.username.asc().nullsLast())
        .fetch();

    Member member5 = result1.get(0);
    Member member6 = result1.get(1);
    Member memberNull = result1.get(2);

    Assertions.assertEquals("member5", member5.getUsername());
    Assertions.assertEquals("member6", member6.getUsername());
    Assertions.assertNull(memberNull.getUsername());
  }

  @Test
  public void paging() {
    List<Member> members = queryFactory
        .selectFrom(member)
        .orderBy(member.username.desc())
        .offset(1)
        .limit(2)
        .fetch();

    Assertions.assertEquals(2, members.size());
  }

  @Test
  public void aggregation() {
    List<Tuple> list = queryFactory
        .select(
            member.count(),
            member.age.sum(),
            member.age.avg(),
            member.age.max(),
            member.age.min()
        )
        .from(member)
        .fetch();

    Tuple tuple = list.get(0);
    Assertions.assertEquals(4, tuple.get(member.count()));
    Assertions.assertEquals(100, tuple.get(member.age.sum()));
    Assertions.assertEquals(25, tuple.get(member.age.avg()));
    Assertions.assertEquals(40, tuple.get(member.age.max()));
    Assertions.assertEquals(10, tuple.get(member.age.min()));
  }

  @DisplayName("팀의 이름과 각 팀의 평균 연령 구하기")
  @Test
  void group() {
    List<Tuple> list = queryFactory
        .select(team.name, member.age.avg())
        .from(member)
        .join(member.team, team)
        .groupBy(team.name)
        .fetch();

    Tuple teamA = list.get(0);
    Tuple teamB = list.get(1);

    Assertions.assertEquals("teamA", teamA.get(team.name));
    Assertions.assertEquals(15, teamA.get(member.age.avg()));

    Assertions.assertEquals("teamB", teamB.get(team.name));
    Assertions.assertEquals(35, teamB.get(member.age.avg()));
  }

  @Test
  void join() {
    List<Member> result = queryFactory
        .selectFrom(member)
        .leftJoin(member.team, team)
        .where(team.name.eq("teamA"))
        .fetch();

    assertThat(result)
        .extracting("username")
        .containsExactly("member1", "member2");
  }

  
  @Test
  void fetchJoin() throws Exception {
    em.flush();
    em.clear();

    Member findMember = queryFactory
        .selectFrom(member)
        .where(member.username.eq("member1"))
        .fetchOne();

    boolean isLoaded = em.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(findMember.getTeam());
    assertThat(isLoaded).as("페치 조인 미적용").isFalse();

    em.flush();
    em.clear();

    findMember = queryFactory
        .selectFrom(member)
        .join(member.team, team).fetchJoin()
        .where(member.username.eq("member1"))
        .fetchOne();

    isLoaded = em.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(findMember.getTeam());
    assertThat(isLoaded).as("페치 조인 적용").isTrue();

  }
}
