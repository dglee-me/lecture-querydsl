package kr.co.dglee.lecture.controller;

import java.util.List;
import kr.co.dglee.lecture.dto.MemberSearchCondition;
import kr.co.dglee.lecture.dto.MemberTeamDTO;
import kr.co.dglee.lecture.repository.MemberJpaRepository;
import kr.co.dglee.lecture.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

  private final MemberJpaRepository memberJpaRepository;

  private final MemberRepository memberRepository;

  @GetMapping("/v1/members")
  public List<MemberTeamDTO> searchMemberV1(MemberSearchCondition condition) {
    return memberJpaRepository.search(condition);
  }

  @GetMapping("/v2/members")
  public Page<MemberTeamDTO> searchMemberV2(MemberSearchCondition condition, Pageable pageable) {
    return memberRepository.searchPageSimple(condition, pageable);
  }

  @GetMapping("/v3/members")
  public Page<MemberTeamDTO> searchMemberV3(MemberSearchCondition condition, Pageable pageable) {
    return memberRepository.searchPageComplex(condition, pageable);
  }
}
