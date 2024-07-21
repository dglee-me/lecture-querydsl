package kr.co.dglee.lecture.repository;

import java.util.List;
import kr.co.dglee.lecture.dto.MemberSearchCondition;
import kr.co.dglee.lecture.dto.MemberTeamDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

  List<MemberTeamDTO> search(MemberSearchCondition condition);
  Page<MemberTeamDTO> searchPageSimple(MemberSearchCondition condition, Pageable pageable);
  Page<MemberTeamDTO> searchPageComplex(MemberSearchCondition condition, Pageable pageable);
}
