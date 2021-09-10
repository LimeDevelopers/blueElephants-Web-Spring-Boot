package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.MemberSchool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface MemberSchoolRepository extends JpaRepository<MemberSchool, Long> {

    MemberSchool findByMberPid(Long mberPid);
    MemberSchool findByAreaNmAndSchlNmAndGradeAndBanAndNo(String areaNm, String schlNm, Integer grade, String ban, Integer no);

    @Procedure(procedureName = "pr_findTID")
    void pr_findTID(String area_nm ,String schl_nm , int grade , String ban , Long spid , LocalDateTime reg_dtm);

}
