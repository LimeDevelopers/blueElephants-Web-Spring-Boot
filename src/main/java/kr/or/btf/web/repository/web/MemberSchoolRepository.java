package kr.or.btf.web.repository.web;

import com.sun.jdi.Value;
import kr.or.btf.web.domain.web.MemberSchool;
import kr.or.btf.web.web.form.MemberSchoolForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Member;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MemberSchoolRepository extends JpaRepository<MemberSchool, Long> {

    MemberSchool findByMberPid(Long mberPid);
    MemberSchool findByAreaNmAndSchlNmAndGradeAndBanAndNo(String areaNm, String schlNm, Integer grade, String ban, Integer no);

    @Procedure(procedureName = "pr_findTID")
    void pr_findTID(String area_nm ,String schl_nm , int grade , String ban , Long spid , LocalDateTime reg_dtm);

}
