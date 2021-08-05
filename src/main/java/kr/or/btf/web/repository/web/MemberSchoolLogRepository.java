package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.MemberSchoolLog;
import kr.or.btf.web.web.form.MemberSchoolForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberSchoolLogRepository extends JpaRepository<MemberSchoolLog, Long> {

    MemberSchoolLog findByMberPid(Long mberPid);

    @Query(value = " SELECT" +
            " nm" +
            " ,area_nm" +
            " ,schl_nm" +
            " ,grade" +
            " ,ban" +
            " ,`no`" +
            " ,mber_pid" +
            " ,reg_dtm" +
            " from" +
                    " (" +
                            " select" +
                            " tm.nm" +
                            " ,tms.area_nm" +
                            " ,tms.schl_nm" +
                            " ,tms.grade" +
                            " ,tms.ban" +
                            " ,tms.`no`" +
                            " ,tms.mber_pid" +
                            " ,tms.reg_dtm" +
                            " from tbl_member_school as tms" +
                            " inner join tbl_member as tm on tms.mber_pid = tm.mber_pid" +
                            " where tms.mber_pid = ?#{#mberPid}" +

                            " union all" +

                            " select" +
                            " tm.nm" +
                            " ,tmsl.area_nm" +
                            " ,tmsl.schl_nm" +
                            " ,tmsl.grade" +
                            " ,tmsl.ban" +
                            " ,tmsl.`no`" +
                            " ,tmsl.mber_pid" +
                            " ,tmsl.reg_dtm" +
                            " from tbl_member_school_log as tmsl" +
                            " inner join tbl_member as tm on tmsl.mber_pid = tm.mber_pid" +
                            " where tmsl.mber_pid = ?#{#mberPid}" +

                    " ) as t" +
            " group by MBER_PID, AREA_NM, schl_nm, grade, ban, `no`" +
            " order by reg_dtm desc" , nativeQuery = true)
    List<Object[]> childSchlLogList(Long mberPid);

    @Query(value = " SELECT" +
            " nm" +
            " ,area_nm" +
            " ,schl_nm" +
            " ,grade" +
            " ,ban" +
            " ,`no`" +
            " ,mber_pid" +
            " ,reg_dtm" +
            " from" +
                    " (" +
                            " select" +
                            " tm.nm" +
                            " ,tms.area_nm" +
                            " ,tms.schl_nm" +
                            " ,tms.grade" +
                            " ,tms.ban" +
                            " ,tms.`no`" +
                            " ,tms.mber_pid" +
                            " ,tms.reg_dtm" +
                            " from tbl_member_school as tms" +
                            " inner join tbl_member as tm on tms.mber_pid = tm.mber_pid" +
                            " where tms.area_nm = ?#{#memberSchoolForm.areaNm} and tms.schl_nm = ?#{#memberSchoolForm.schlNm} and tms.grade = ?#{#memberSchoolForm.grade} and tms.ban = ?#{#memberSchoolForm.ban} and tms.teacher_nm = ?#{#memberSchoolForm.teacherNm}" +

                            " union all" +

                            " select" +
                            " tm.nm" +
                            " ,tmsl.area_nm" +
                            " ,tmsl.schl_nm" +
                            " ,tmsl.grade" +
                            " ,tmsl.ban" +
                            " ,tmsl.`no`" +
                            " ,tmsl.mber_pid" +
                            " ,tmsl.reg_dtm" +
                            " from tbl_member_school_log as tmsl" +
                            " inner join tbl_member as tm on tmsl.mber_pid = tm.mber_pid" +
                            " where tmsl.area_nm = ?#{#memberSchoolForm.areaNm} and tmsl.schl_nm = ?#{#memberSchoolForm.schlNm} and tmsl.grade = ?#{#memberSchoolForm.grade} and tmsl.ban = ?#{#memberSchoolForm.ban} and tmsl.teacher_nm = ?#{#memberSchoolForm.teacherNm}" +

                    " ) as t" +
            " group by MBER_PID, AREA_NM, schl_nm, grade, ban, `no`" +
            " order by `no` asc", nativeQuery = true)
    List<Object[]> childLogList(MemberSchoolForm memberSchoolForm);
}
