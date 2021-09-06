package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.web.form.CourseRequestForm;
import kr.or.btf.web.web.form.SearchForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Account, Long> {

    Account findByLoginIdAndDelAt(String loginId, String delAt);

    boolean existsByLoginId(String loginId);

    boolean existsByEmailAndEmailAttcAt(String email, String emailAttcAt);

    Account findByNmAndEmail(String nm, String email);

    Account findByLoginIdAndEmail(String loginId, String email);

    Optional<Account> findByLoginId(String loginId);

    Optional<Account> findByEmail(String email);

    @Query(value = "select count(*) as cVal,\n" +
            "       ageRange as category\n" +
            " from (\n" +
            "         select (case\n" +
            "                     when age < 20 then '20대미만'\n" +
            "                     when age >= 20 and age < 30 then '20대'\n" +
            "                     when age >= 30 and age < 40 then '30대'\n" +
            "                     when age >= 40 and age < 50 then '40대'\n" +
            "                     when age >= 50 then '50대이상'\n" +
            "                     else '미지정' end) as ageRange" +
            ", (case\n" +
            "                     when age < 20 then 1\n" +
            "                     when age >= 20 and age < 30 then 2\n" +
            "                     when age >= 30 and age < 40 then 3\n" +
            "                     when age >= 40 and age < 50 then 4\n" +
            "                     when age >= 50 then 5\n" +
            "                     else 6 end) as ageSrt\n" +
            "         from (\n" +
            "                  SELECT ifnull(DATE_FORMAT(now(), '%Y') - substring(tm.brthdy, 1, 4) + 1, 0) as age\n" +
            "                  from tbl_member as tm\n" +
            "                  where tm.brthdy is not null\n" +
            "              ) as tms\n" +
            "     ) as tmss\n" +
            " group by ageRange order by ageSrt", nativeQuery = true)
    List<Object[]> ageResult();

    @Query(value = "select * from (\n" +
            "         select count(*),\n" +
            "                (case\n" +
            "                     when mber_dv_ty = 'ADMIN' then '관리자'\n" +
            "                     when mber_dv_ty = 'COUNSELOR' then '상담사'\n" +
            "                     when mber_dv_ty = 'LECTURER' then '강사'\n" +
            "                     when mber_dv_ty = 'MASTER' then '전체관리자'\n" +
            "                     when mber_dv_ty = 'NORMAL' then '일반'\n" +
            "                     when mber_dv_ty = 'PARENT' then '부모'\n" +
            "                     when mber_dv_ty = 'STUDENT' then '학생'\n" +
            "                     when mber_dv_ty = 'TEACHER' then '교원'\n" +
            "                     else 0\n" +
            "                    end\n" +
            "                    ) as mber_dv_nm,\n" +
            "                mber_dv_ty,\n" +
            "                (case\n" +
            "                     when mber_dv_ty = 'ADMIN' then 7\n" +
            "                     when mber_dv_ty = 'COUNSELOR' then 6\n" +
            "                     when mber_dv_ty = 'LECTURER' then 5\n" +
            "                     when mber_dv_ty = 'MASTER' then 8\n" +
            "                     when mber_dv_ty = 'NORMAL' then 4\n" +
            "                     when mber_dv_ty = 'PARENT' then 2\n" +
            "                     when mber_dv_ty = 'STUDENT' then 1\n" +
            "                     when mber_dv_ty = 'TEACHER' then 3\n" +
            "                     else 0\n" +
            "                    end\n" +
            "                    ) as mber_srt\n" +
            "         from tbl_member\n" +
            "         group by mber_dv_ty\n" +
            "     ) as T\n" +
            " where T.mber_dv_ty in ('COUNSELOR', 'LECTURER', 'NORMAL', 'PARENT', 'STUDENT', 'TEACHER')\n" +
            " order by T.mber_srt", nativeQuery = true)
    List<Object[]> typeResult();


    @Query(value = "select" +
            " result.value ," +
            " result.regYear," +
            " result.regYearMonth" +
            " from" +
                    " (select" +
                            " count(*) as value," +
                            " DATE_FORMAT(tm.reg_dtm, '%Y') as regYear," +
                            " DATE_FORMAT(tm.reg_dtm, '%Y.%m') as regYearMonth" +
                    " from tbl_member as tm" +
                    " group by DATE_FORMAT(tm.reg_dtm, '%Y.%m')) as result" +
            " where result.regYear = ?#{#srchYear}" +
            " group by result.regYearMonth", nativeQuery = true)
    List<Object[]> monthResult(String srchYear);

    @Query(value = "select" +
            " dateField" +
            " ,sum(cnt)" +
            " from" +
            " (" +
            " select" +
            " count(*) as cnt," +
            " cnct_id," +
            " date_format(cnct_dtm, '%Y-%m') as dateField" +
            " from" +
            " tbl_login_cnnt_logs as tll" +
            " where succes_at = 'Y'" +
            " and date_format(cnct_dtm, '%Y') = ?#{#srchYear}" +
            " group by cnct_id, date_format(cnct_dtm, '%Y-%m')" +
            " order by date_format(cnct_dtm, '%Y-%m') asc" +
            " ) as t" +
            " group by dateField", nativeQuery = true)
    List<Object[]> monthConnectResult(String srchYear);

    @Query(value = "select" +
            " dateField" +
            " ,sum(cnt)" +
            " from" +
            " (" +
            " select" +
            " count(*) as cnt," +
            " cnct_id," +
            " date_format(cnct_dtm, '%Y-%m-%d') as dateField" +
            " from" +
            " tbl_login_cnnt_logs as tll" +
            " where succes_at = 'Y'" +
            " and date_format(cnct_dtm, '%Y-%m') = ?#{#srchYearMonth}" +
            " group by cnct_id, date_format(cnct_dtm, '%Y-%m-%d')" +
            " order by date_format(cnct_dtm, '%Y-%m-%d') asc" +
            " ) as t" +
            " group by dateField", nativeQuery = true)
    List<Object[]> dayConnectResult(String srchYearMonth);

    @Query(value= "select * from (\n" +
            "         select count(*),\n" +
            "                (case\n" +
            "                     when mber_dv_ty = 'ADMIN' then '관리자'\n" +
            "                     when mber_dv_ty = 'COUNSELOR' then '상담사'\n" +
            "                     when mber_dv_ty = 'LECTURER' then '강사'\n" +
            "                     when mber_dv_ty = 'MASTER' then '전체관리자'\n" +
            "                     when mber_dv_ty = 'NORMAL' then '일반'\n" +
            "                     when mber_dv_ty = 'PARENT' then '부모'\n" +
            "                     when mber_dv_ty = 'STUDENT' then '학생'\n" +
            "                     when mber_dv_ty = 'TEACHER' then '교원'\n" +
            "                     else 0\n" +
            "                    end\n" +
            "                    ) as mber_dv_nm,\n" +
            "                mber_dv_ty,\n" +
            "                (case\n" +
            "                     when mber_dv_ty = 'ADMIN' then 7\n" +
            "                     when mber_dv_ty = 'COUNSELOR' then 6\n" +
            "                     when mber_dv_ty = 'LECTURER' then 5\n" +
            "                     when mber_dv_ty = 'MASTER' then 8\n" +
            "                     when mber_dv_ty = 'NORMAL' then 4\n" +
            "                     when mber_dv_ty = 'PARENT' then 2\n" +
            "                     when mber_dv_ty = 'STUDENT' then 1\n" +
            "                     when mber_dv_ty = 'TEACHER' then 3\n" +
            "                     else 0\n" +
            "                    end\n" +
            "                    ) as mber_srt\n" +
            "         from tbl_advice_reservation\n" +
            "         where date_format(reg_dtm, '%Y-%m-%d') between ?#{#searchForm.srchStDt} and ?#{#searchForm.srchEdDt}\n" +
            "         and area_code_pid in (?#{#searchForm.srchCodePidArr})\n" +
            //"         and dvTy in (?#{#searchForm.reservationDvTy})\n" +
            "         group by mber_dv_ty\n" +
            "     ) as T\n" +
            " where T.mber_dv_ty in ('COUNSELOR', 'LECTURER', 'NORMAL', 'PARENT', 'STUDENT', 'TEACHER')\n" +
            " order by T.mber_srt", nativeQuery = true)
    List<Object[]> memberTypeToAdviceResult(SearchForm searchForm);

    @Query(value="select * from (\n" +
            "         select count(*),\n" +
            "                (case\n" +
            "                     when mber_dv_ty = 'ADMIN' then '관리자'\n" +
            "                     when mber_dv_ty = 'COUNSELOR' then '상담사'\n" +
            "                     when mber_dv_ty = 'LECTURER' then '강사'\n" +
            "                     when mber_dv_ty = 'MASTER' then '전체관리자'\n" +
            "                     when mber_dv_ty = 'NORMAL' then '일반'\n" +
            "                     when mber_dv_ty = 'PARENT' then '부모'\n" +
            "                     when mber_dv_ty = 'STUDENT' then '학생'\n" +
            "                     when mber_dv_ty = 'TEACHER' then '교원'\n" +
            "                     else 0\n" +
            "                    end\n" +
            "                    ) as mber_dv_nm,\n" +
            "                mber_dv_ty,\n" +
            "                (case\n" +
            "                     when mber_dv_ty = 'ADMIN' then 7\n" +
            "                     when mber_dv_ty = 'COUNSELOR' then 6\n" +
            "                     when mber_dv_ty = 'LECTURER' then 5\n" +
            "                     when mber_dv_ty = 'MASTER' then 8\n" +
            "                     when mber_dv_ty = 'NORMAL' then 4\n" +
            "                     when mber_dv_ty = 'PARENT' then 2\n" +
            "                     when mber_dv_ty = 'STUDENT' then 1\n" +
            "                     when mber_dv_ty = 'TEACHER' then 3\n" +
            "                     else 0\n" +
            "                    end\n" +
            "                    ) as mber_srt\n" +
            "         from tbl_course_request as crs\n" +
            "         inner join tbl_member tm on crs.mber_pid = tm.mber_pid\n" +
            "         where date_format(crs.reg_dtm, '%Y-%m-%d') between ?#{#searchForm.srchStDt} and ?#{#searchForm.srchEdDt}\n" +
            "         and crs.area_nm in (?#{#searchForm.srchWordArr})\n" +
            "         group by mber_dv_ty\n" +
            "     ) as T\n" +
            " where T.mber_dv_ty in ('COUNSELOR', 'LECTURER', 'NORMAL', 'PARENT', 'STUDENT', 'TEACHER')\n" +
            " order by T.mber_srt", nativeQuery = true)
    List<Object[]> memberTypeToCourseResult(SearchForm searchForm);


    @Query(value="select * from (\n" +
            "                  select count(*),\n" +
            "                         ifnull(crs.area_nm,'')  as area_nm,\n" +
            "                         ifnull(crs.schl_nm,'')  as schl_nm\n" +
            "                  from (SELECT * from tbl_course_request\n" +
            "                       where schl_nm is not null and schl_nm != ''\n" +
            "                       AND date_format(reg_dtm, '%Y-%m-%d') between ?#{#form.srchStDt} and ?#{#form.srchEdDt}\n" +
            "                       AND (?#{#form.areaNm} = '' or area_nm = ?#{#form.areaNm})\n" +
            "                       AND (?#{#form.schlNm} = '' or schl_nm = ?#{#form.schlNm})\n" +
            "                       AND (?#{#form.grade} is null or grade = ?#{#form.grade})\n" +
            "                       AND (?#{#form.ban} = '' or ban = ?#{#form.ban})\n" +
            "                       AND (?#{#form.no} is null or no = ?#{#form.no})\n" +
            "                       ) as crs\n" +
            "                           inner join tbl_member tm on crs.mber_pid = tm.mber_pid\n" +
            "                           inner join tbl_course_request_complete tcrc on crs.atnlc_req_pid = tcrc.atnlc_req_pid\n" +
            "                           AND tcrc.cmpl_stt_ty = 'COMPLETE' AND tcrc.sn = ?#{#form.sn}\n" +
            "                  group by crs.area_nm, crs.schl_nm\n" +
            "              ) as T\n" +
            "order by T.area_nm, T.schl_nm", nativeQuery = true)
    List<Object[]> courseCompleteStatus(CourseRequestForm form);

    @Query(value="SELECT\n" +
            "  COUNT(*),\n" +
            "  (case\n" +
            "      when per.mber_dv_ty = 'ADMIN' then '관리자'\n" +
            "      when per.mber_dv_ty = 'COUNSELOR' then '상담사'\n" +
            "      when per.mber_dv_ty = 'LECTURER' then '강사'\n" +
            "      when per.mber_dv_ty = 'MASTER' then '전체관리자'\n" +
            "      when per.mber_dv_ty = 'NORMAL' then '일반'\n" +
            "      when per.mber_dv_ty = 'PARENT' then '부모'\n" +
            "      when per.mber_dv_ty = 'STUDENT' then '학생'\n" +
            "      when per.mber_dv_ty = 'TEACHER' then '교원'\n" +
            "      else 0\n" +
            "     end\n" +
            "     ) as mber_dv_nm,\n" +
            " per.mber_dv_ty,\n" +
            " (case\n" +
            "      when per.mber_dv_ty = 'ADMIN' then 7\n" +
            "      when per.mber_dv_ty = 'COUNSELOR' then 6\n" +
            "      when per.mber_dv_ty = 'LECTURER' then 5\n" +
            "      when per.mber_dv_ty = 'MASTER' then 8\n" +
            "      when per.mber_dv_ty = 'NORMAL' then 4\n" +
            "      when per.mber_dv_ty = 'PARENT' then 2\n" +
            "      when per.mber_dv_ty = 'STUDENT' then 1\n" +
            "      when per.mber_dv_ty = 'TEACHER' then 3\n" +
            "      else 0\n" +
            "     end\n" +
            "     ) as mber_srt\n" +
            " FROM (SELECT * FROM tbl_survey_response_person\n" +
            "   where mber_dv_ty in ('COUNSELOR', 'LECTURER', 'NORMAL', 'PARENT', 'STUDENT', 'TEACHER')\n" +
            "   and date_format(reg_dtm, '%Y-%m-%d') between ?#{#form.srchStDt} and ?#{#form.srchEdDt}\n" +
            "  ) AS per\n" +
            " INNER JOIN tbl_survey AS sur ON per.qustnr_pid = sur.qustnr_pid AND sur.dv_ty in (?#{#form.surveyDvTypes})\n" +
            " group by per.mber_dv_ty", nativeQuery = true)
    List<Object[]> surveyStatusResult(SearchForm form);

    @Query(value="with recursive answer AS (\n" +
            "  SELECT \n" +
            "   ait.*\n" +
            "   ,sv.qustnr_pid\n" +
            "  FROM tbl_survey AS sv\n" +
            "  INNER JOIN tbl_survey_question_item AS qit ON sv.qustnr_pid = qit.qustnr_pid AND qit.del_at = 'N'  AND qit.asw_dv_ty = 'CHOICE'\n" +
            "  INNER JOIN tbl_survey_answer_item AS ait ON qit.qesitm_pid = ait.qesitm_pid AND ait.del_at ='N'\n" +
            "  WHERE sv.dv_ty in (?#{#form.surveyDvTypes}) AND sv.del_at = 'N'\n" +
            "  ORDER BY qesitm_pid, asw_pid\n" +
            " )\n" +
            " \n" +
            " SELECT\n" +
            "  COUNT(*)\n" +
            "  ,grade\n" +
            " FROM\n" +
            " (\n" +
            "  SELECT\n" +
            "   login_id\n" +
            "   ,ROUND(case when MOD(grade, 20) = 0 then (grade / 20) ELSE (grade / 20 + 1) END) AS grade\n" +
            "  FROM\n" +
            "  (\n" +
            "   SELECT\n" +
            "    login_id\n" +
            "    ,(case when SUM(pt) = 0 then 0 ELSE (SUM(pt) / (COUNT(*) * 5) * 100) END) AS grade\n" +
            "   FROM\n" +
            "   (\n" +
            "    SELECT \n" +
            "     per.login_id\n" +
            "     ,(ans.asw_pid-p+1) AS pt\n" +
            "    FROM answer AS ans\n" +
            "    INNER JOIN (SELECT qesitm_pid, asw_pid AS p FROM answer GROUP BY qesitm_pid) AS p ON ans.qesitm_pid = p.qesitm_pid\n" +
            "    INNER join tbl_survey_response_person AS per ON ans.qustnr_pid = per.qustnr_pid AND per.del_at = 'N' \n" +
            "    AND per.mber_dv_ty IN (?#{#form.userRollTypes})\n" +
            //"    #AND per.login_id = ''\n" +
            "    INNER JOIN tbl_survey_response AS res ON per.rsp_ps_pid = res.rsp_ps_pid AND ans.qesitm_pid = res.qesitm_pid AND ans.asw_pid = res.asw_pid\n" +
            "    GROUP BY login_id\n" +
            "   ) AS t\n" +
            "   GROUP BY login_id\n" +
            "  ) AS tt\n" +
            " ) AS ttt\n" +
            " GROUP BY grade\n" +
            " ORDER BY grade", nativeQuery = true)
    List<Object[]> surveyResponseResult(SearchForm form);

    @Query(value="SELECT\n" +
            "   COUNT(*)\n" +
            "   ,pum.menu_nm\n" +
            " FROM tbl_action_log AS ac\n" +
            " INNER JOIN tbl_user_menu AS um ON ac.cnct_url = um.menu_url AND um.del_at = 'N'\n" +
            " INNER JOIN tbl_user_menu AS pum ON um.parnts_menu_pid = pum.menu_pid AND pum.parnts_menu_pid = 2\n" +
            " INNER JOIN tbl_member AS mem ON ac.mber_pid = mem.mber_pid AND mem.mber_dv_ty in (?#{#form.userRollTypes})\n" +
            " WHERE ac.act_dv_ty = 'INNER_LINK'\n" +
            "   and date_format(ac.act_dtm, '%Y-%m-%d') between ?#{#form.srchStDt} and ?#{#form.srchEdDt}\n" +
            " GROUP BY pum.menu_nm\n" +
            " ORDER BY pum.menu_nm", nativeQuery = true)
    List<Object[]> menuStatusResult(SearchForm form);

    @Modifying
    @Query("UPDATE Account ac SET ac.approval = :yn WHERE ac.id = :id")
    int setApproval(Long id, String yn);

    @Modifying
    @Query("UPDATE Account ac SET ac.onlineEdu = :yn WHERE ac.id = :id")
    int setEduType(Long id, String yn);

/*
    @Query(
            value = " update TBL_MNG_INFO set lstCnntDtm=sysdate()\n" +
                    "where mngPid = :mngPid ",
            nativeQuery = true
    )
    void updateLastLogin(long mngPid);*/
}
