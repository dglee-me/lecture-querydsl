package kr.co.dglee.lecture.repository;

import java.util.List;
import kr.co.dglee.lecture.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

  List<Member> findByUsername(String username);
}
