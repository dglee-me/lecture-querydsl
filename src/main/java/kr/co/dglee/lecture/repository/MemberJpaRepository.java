package kr.co.dglee.lecture.repository;

import static kr.co.dglee.lecture.entity.QMember.member;
import static kr.co.dglee.lecture.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import kr.co.dglee.lecture.dto.MemberSearchCondition;
import kr.co.dglee.lecture.dto.MemberTeamDTO;
import kr.co.dglee.lecture.dto.QMemberTeamDTO;
import kr.co.dglee.lecture.entity.Member;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class MemberJpaRepository {

  private final EntityManager em;

  private final JPAQueryFactory queryFactory;

  public MemberJpaRepository(EntityManager em) {
    this.em = em;
    this.queryFactory = new JPAQueryFactory(em);
  }

  public void save(Member member) {
    em.persist(member);
  }

  public Optional<Member> findById(Long id) {
    return Optional.ofNullable(em.find(Member.class, id));
  }

  public List<Member> findAll() {
    return em.createQuery("SELECT m FROM Member m", Member.class)
        .getResultList();
  }

  public List<Member> findAllByQueryDSL() {
    return queryFactory
        .selectFrom(member)
        .fetch();
  }

  public List<Member> findAllByUsername(String username) {
    return em.createQuery("SELECT m FROM Member m WHERE m.username = :username", Member.class)
        .setParameter("username", username)
        .getResultList();
  }

  public List<Member> findAllByUsernameByQueryDSL(String username) {
    return queryFactory
        .selectFrom(member)
        .where(member.username.eq(username))
        .fetch();
  }

  public List<MemberTeamDTO> searchByBuilder(MemberSearchCondition condition) {

    BooleanBuilder builder = new BooleanBuilder();

    if (hasText(condition.getUsername())) {
      builder.and(member.username.eq(condition.getUsername()));
    }

    if (hasText(condition.getTeamName())) {
      builder.and(team.name.eq(condition.getTeamName()));
    }

    if (condition.getAgeGoe() != null) {
      builder.and(member.age.goe(condition.getAgeGoe()));
    }

    if (condition.getAgeLoe() != null) {
      builder.and(member.age.loe(condition.getAgeLoe()));
    }

    return queryFactory
        .select(new QMemberTeamDTO(
            member.id.as("memberId"),
            member.username,
            member.age,
            team.id.as("teamId"),
            team.name.as("teamName")))
        .from(member)
        .leftJoin(member.team, team)
        .where(builder)
        .fetch();
  }

  public List<MemberTeamDTO> search(MemberSearchCondition condition) {
    return queryFactory
        .select(new QMemberTeamDTO(
            member.id.as("memberId"),
            member.username,
            member.age,
            team.id.as("teamId"),
            team.name.as("teamName")))
        .from(member)
        .leftJoin(member.team, team)
        .where(
            usernameEq(condition.getUsername()),
            teamNameEq(condition.getTeamName()),
            ageGoe(condition.getAgeGoe()),
            ageLoe(condition.getAgeLoe())
        )
        .fetch();
  }

  private BooleanExpression usernameEq(String username) {
    return hasText(username) ? member.username.eq(username) : null;
  }

  private BooleanExpression teamNameEq(String teamName){
    return hasText(teamName) ? team.name.eq(teamName) : null;
  }

  private BooleanExpression ageGoe(Integer ageGoe){
    return ageGoe != null ? member.age.goe(ageGoe) : null;
  }

  private BooleanExpression ageLoe(Integer ageLoe) {
    return ageLoe != null ? member.age.loe(ageLoe) : null;
  }
}
