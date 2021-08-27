package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.MemberSchool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberSchoolRepository extends JpaRepository<MemberSchool, Long> {

    MemberSchool findByMberPid(Long mberPid);
    MemberSchool findByAreaNmAndSchlNmAndGradeAndBanAndNo(String areaNm, String schlNm, Integer grade, String ban, Integer no);

    @Procedure
    MemberSchool pr_findTid(String area_nm ,String schl_nm , int grade , String ban);
}
