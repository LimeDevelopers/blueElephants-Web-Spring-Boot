package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.MemberSchool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberSchoolRepository extends JpaRepository<MemberSchool, Long> {

    MemberSchool findByMberPid(Long mberPid);
    MemberSchool findByAreaNmAndSchlNmAndGradeAndBanAndNo(String areaNm, String schlNm, Integer grade, String ban, Integer no);
}
