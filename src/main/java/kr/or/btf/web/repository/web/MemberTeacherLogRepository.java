package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.MemberTeacherLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberTeacherLogRepository extends JpaRepository<MemberTeacherLog, Long> {

    MemberTeacherLog findByMberPid(Long mberPid);

    @Query(value = " SELECT" +
            " t.nm" +
            " ,t.mber_pid" +
            " ,t.area_nm" +
            " ,t.schl_nm" +
            " ,t.grade" +
            " ,t.ban" +
            " ,t.reg_dtm" +
            " ,(case when t1.cnt IS NULL then 0 else t1.cnt END) AS studentCnt" +
    " FROM (" +
            " SELECT" +
                    " tm.nm," +
            " tmt.mber_pid ," +
            " tmt.area_nm," +
            " tmt.schl_nm," +
            " tmt.grade," +
            " tmt.ban," +
            " tmt.reg_dtm" +
                    " from tbl_member_teacher as tmt" +
                    " inner join tbl_member as tm on tmt.mber_pid = tm.mber_pid" +
                    " where tmt.mber_pid = ?#{#mberPid}" +

                    " union all" +

                    " select" +
                    " tm.nm," +
            " tmtl.mber_pid ," +
            " tmtl.area_nm ," +
            " tmtl.schl_nm ," +
            " tmtl.grade ," +
            " tmtl.ban ," +
            " tmtl.reg_dtm" +
                    " from tbl_member_teacher_log as tmtl" +
                    " inner join tbl_member as tm on tmtl.mber_pid = tm.mber_pid" +
                    " where tmtl.mber_pid = ?#{#mberPid}" +
    " ) as t" +
    " LEFT JOIN (" +
            " SELECT" +
                    " schl_nm" +
            " ,grade" +
            " ,ban" +
            " ,teacher_nm" +
            " ,area_nm" +
            " ,COUNT(*) AS cnt" +
    " FROM (" +
            " select" +
                    " schl_nm" +
            " ,grade" +
            " ,ban" +
            " ,teacher_nm" +
            " ,area_nm" +
                    " FROM tbl_member_school" +

                    " UNION all" +

                    " select" +
                    " schl_nm" +
            " ,grade" +
            " ,ban" +
            " ,teacher_nm" +
            " ,area_nm" +
                    " FROM tbl_member_school_log" +
    " ) AS tt" +
    " GROUP BY schl_nm,grade,ban,teacher_nm,area_nm" +
" ) AS t1 ON t.schl_nm = t1.schl_nm and t.grade = t1.grade and t.ban = t1.ban AND t.nm=t1.teacher_nm and t.area_nm=t1.area_nm" +
    " group BY t.mber_pid , t.area_nm , t.schl_nm , t.grade , t.ban" +
    " order BY t.reg_dtm desc" , nativeQuery = true)
    List<Object[]> schlLogList(Long mberPid);

}
