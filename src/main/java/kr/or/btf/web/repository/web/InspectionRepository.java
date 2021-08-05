package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.Inspection;
import kr.or.btf.web.web.form.InspectionForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    @Query(value = "SELECT" +
            "  t3.code_nm" +
            "  ,t3.dv_code_pid" +
            "  ,t3.mVal" +
            "  ,rst.cnts" +
            "  FROM" +
            "  (" +
            "   SELECT" +
            "     code_nm" +
            "     ,dv_code_pid" +
            "     ,(" +
            "      case " +
            "      when dv_code_pid = ?#{#inspectionForm.dvCodePid1} AND tot >= ?#{#inspectionForm.dvLimitCnt1} then 1 " +
            "      when dv_code_pid = ?#{#inspectionForm.dvCodePid2} AND tot >= ?#{#inspectionForm.dvLimitCnt2} then 1" +
            "      when dv_code_pid = ?#{#inspectionForm.dvCodePid3} AND tot >= ?#{#inspectionForm.dvLimitCnt3} then 1" +
            "      when dv_code_pid = ?#{#inspectionForm.dvCodePid4} AND tot >= ?#{#inspectionForm.dvLimitCnt4} then 1" +
            "      ELSE 0 " +
            "      END" +
            "     ) AS mVal" +
            "    FROM" +
            "    (" +
            "     SELECT" +
            "       mber_pid" +
            "       ,code_nm" +
            "       ,dv_code_pid" +
            "       ,sum(case when sn is not null then (sn-1) ELSE 0 end) AS tot" +
            "     FROM" +
            "     (" +
            "      SELECT" +
            "       req.mber_pid" +
            "       ,cd.code_nm" +
            "       ,qus.dv_code_pid" +
            "       ,ans.sn" +
            "      FROM tbl_course_request AS req" +
            "      INNER JOIN tbl_course_request_complete AS cpl " +
            "       ON req.atnlc_req_pid = cpl.atnlc_req_pid " +
            "       AND req.crs_mst_pid = cpl.crs_mst_pid" +
            "       AND cpl.crs_pid = ?#{#inspectionForm.id}" +
            "       AND cpl.sn = ?#{#inspectionForm.sn}" +
            "       AND cpl.cmpl_stt_ty = 'COMPLETE'" +
            "      INNER JOIN tbl_inspection_response_person AS per" +
            "       ON req.atnlc_req_pid = per.atnlc_req_pid" +
            "       AND per.inspct_pid = ?#{#inspectionForm.id}" +
            "       AND per.del_at = 'N'" +
            "       AND per.inspct_dv_ty = ?#{#inspectionForm.inspctDvTy}" +
            "      INNER JOIN tbl_inspection AS inp" +
            "       ON per.inspct_pid = inp.inspct_pid" +
            "      INNER JOIN tbl_inspection_question_item AS qus" +
            "       ON inp.inspct_pid = qus.inspct_pid" +
            "       AND qus.dv_code_pid IN (?#{#inspectionForm.dvCodePid1}, ?#{#inspectionForm.dvCodePid2}, ?#{#inspectionForm.dvCodePid3}, ?#{#inspectionForm.dvCodePid4})" +
            "       AND qus.del_at = 'N'" +
            "      INNER JOIN tbl_inspection_answer_item AS ans" +
            "       ON qus.qesitm_pid = ans.qesitm_pid" +
            "       AND ans.del_at = 'N'" +
            "      INNER JOIN tbl_inspection_response AS res" +
            "       ON qus.qesitm_pid = res.qesitm_pid" +
            "       AND per.rsp_ps_pid = res.rsp_ps_pid" +
            "       AND ans.asw_pid = res.asw_pid" +
            "      INNER JOIN tbl_common_code AS cd" +
            "       ON qus.dv_code_pid = cd.code_pid" +
            "       AND cd.del_at = 'N'" +
            "      WHERE req.area_nm = ?#{#inspectionForm.areaNm}" +
            "      AND req.schl_nm = ?#{#inspectionForm.schlNm}" +
            "      AND req.grade = ?#{#inspectionForm.grade}" +
            "      AND req.ban = ?#{#inspectionForm.ban}" +
            "      AND req.crs_mst_pid = ?#{#inspectionForm.crsMstPid}" +
            "      AND req.mber_pid = ?#{#inspectionForm.mberPid}" +
            "     ) AS t" +
            "     GROUP BY mber_pid, code_nm, dv_code_pid" +
            "    ) AS t2" +
            "    ORDER BY dv_code_pid" +
            "  ) AS t3" +
            "  INNER JOIN tbl_inspection_result AS rst" +
            "   ON t3.dv_code_pid = rst.dv_code_pid" +
            "   AND t3.mVal = rst.sn" +
            "  ORDER BY t3.dv_code_pid"
            , nativeQuery = true)
    List<Object[]> myInspectionResult1_1(InspectionForm inspectionForm);

    @Query(value = "SELECT" +
            "  code_nm" +
            "  ,sum(" +
            "   case " +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid1} AND mber_pid = ?#{#inspectionForm.mberPid} then 100/?#{#inspectionForm.dvQuestionCnt1}*tot " +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid2} AND mber_pid = ?#{#inspectionForm.mberPid} then 100/?#{#inspectionForm.dvQuestionCnt2}*tot" +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid3} AND mber_pid = ?#{#inspectionForm.mberPid} then 100/?#{#inspectionForm.dvQuestionCnt3}*tot" +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid4} AND mber_pid = ?#{#inspectionForm.mberPid} then 100/?#{#inspectionForm.dvQuestionCnt4}*tot" +
            "   ELSE 0 " +
            "   END" +
            "  ) AS mVal" +
            "  ,avg(" +
            "   case " +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid1} then 100/?#{#inspectionForm.dvQuestionCnt1}*tot " +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid2} then 100/?#{#inspectionForm.dvQuestionCnt2}*tot" +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid3} then 100/?#{#inspectionForm.dvQuestionCnt3}*tot" +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid4} then 100/?#{#inspectionForm.dvQuestionCnt4}*tot" +
            "   ELSE 0 " +
            "   END" +
            "  ) AS cVal" +
            " FROM" +
            " (" +
            "  SELECT" +
            "    mber_pid" +
            "    ,code_nm" +
            "    ,dv_code_pid" +
            "    ,sum(case when sn is not null then (sn-1) ELSE 0 end) AS tot" +
            "  FROM" +
            "  (" +
            "   SELECT" +
            "    req.mber_pid" +
            "    ,cd.code_nm" +
            "    ,qus.dv_code_pid" +
            "    ,ans.sn" +
            "   FROM tbl_course_request AS req" +
            "   INNER JOIN tbl_course_request_complete AS cpl " +
            "    ON req.atnlc_req_pid = cpl.atnlc_req_pid " +
            "    AND req.crs_mst_pid = cpl.crs_mst_pid" +
            "    AND cpl.crs_pid = ?#{#inspectionForm.id}" +
            "    AND cpl.sn = ?#{#inspectionForm.sn}" +
            "    AND cpl.cmpl_stt_ty = 'COMPLETE'" +
            "   INNER JOIN tbl_inspection_response_person AS per" +
            "    ON req.atnlc_req_pid = per.atnlc_req_pid" +
            "    AND per.inspct_pid = ?#{#inspectionForm.id}" +
            "    AND per.del_at = 'N'" +
            "    AND per.inspct_dv_ty = ?#{#inspectionForm.inspctDvTy}" +
            "   INNER JOIN tbl_inspection AS inp" +
            "    ON per.inspct_pid = inp.inspct_pid" +
            "   INNER JOIN tbl_inspection_question_item AS qus" +
            "    ON inp.inspct_pid = qus.inspct_pid" +
            "    AND qus.dv_code_pid IN (?#{#inspectionForm.dvCodePid1}, ?#{#inspectionForm.dvCodePid2}, ?#{#inspectionForm.dvCodePid3}, ?#{#inspectionForm.dvCodePid4})" +
            "    AND qus.del_at = 'N'" +
            "   INNER JOIN tbl_inspection_answer_item AS ans" +
            "    ON qus.qesitm_pid = ans.qesitm_pid" +
            "    AND ans.del_at = 'N'" +
            "   INNER JOIN tbl_inspection_response AS res" +
            "    ON qus.qesitm_pid = res.qesitm_pid" +
            "    AND per.rsp_ps_pid = res.rsp_ps_pid" +
            "    AND ans.asw_pid = res.asw_pid" +
            "   INNER JOIN tbl_common_code AS cd" +
            "    ON qus.dv_code_pid = cd.code_pid" +
            "    AND cd.del_at = 'N'" +
            "   WHERE req.area_nm = ?#{#inspectionForm.areaNm}" +
            "   AND req.schl_nm = ?#{#inspectionForm.schlNm}" +
            "   AND req.grade = ?#{#inspectionForm.grade}" +
            "   AND req.ban = ?#{#inspectionForm.ban}" +
            "   AND req.crs_mst_pid = ?#{#inspectionForm.crsMstPid}" +
            "  ) AS t" +
            "  GROUP BY mber_pid, code_nm, dv_code_pid" +
            " ) AS t2" +
            " GROUP BY code_nm" +
            " ORDER BY dv_code_pid"
            , nativeQuery = true)
    List<Object[]> classCourseInspectionResult1_1(InspectionForm inspectionForm);

    @Query(value = "SELECT" +
            "  cd.code_nm" +
            "  ,qus.dv_code_pid" +
            "  ,qus.qesitm_pid" +
            "  ,qus.qestn_qesitm" +
            "  ,ans.answer_cnts" +
            "  ,SUM(case when res.asw_pid IS NULL then 0 ELSE 1 END) AS cVal" +
            " FROM tbl_course_request AS req" +
            " INNER JOIN tbl_course_request_complete AS cpl " +
            "  ON req.atnlc_req_pid = cpl.atnlc_req_pid " +
            "  AND req.crs_mst_pid = cpl.crs_mst_pid" +
            "  AND cpl.crs_pid = ?#{#inspectionForm.id}" +
            "  AND cpl.sn = ?#{#inspectionForm.sn}" +
            "  AND cpl.cmpl_stt_ty = 'COMPLETE'" +
            " INNER JOIN tbl_inspection_response_person AS per" +
            "  ON req.atnlc_req_pid = per.atnlc_req_pid" +
            "  AND per.inspct_pid = ?#{#inspectionForm.id}" +
            "  AND per.del_at = 'N'" +
            "  AND per.inspct_dv_ty = ?#{#inspectionForm.inspctDvTy}" +
            " INNER JOIN tbl_inspection AS inp" +
            "  ON per.inspct_pid = inp.inspct_pid" +
            " INNER JOIN tbl_inspection_question_item AS qus" +
            "  ON inp.inspct_pid = qus.inspct_pid" +
            "  AND qus.dv_code_pid IN (?#{#inspectionForm.dvCodePid1})" +
            "  AND qus.del_at = 'N'" +
            " INNER JOIN tbl_inspection_answer_item AS ans" +
            "  ON qus.qesitm_pid = ans.qesitm_pid" +
            "  AND ans.del_at = 'N'" +
            " left JOIN tbl_inspection_response AS res" +
            "  ON qus.qesitm_pid = res.qesitm_pid" +
            "  AND per.rsp_ps_pid = res.rsp_ps_pid" +
            "  AND ans.asw_pid = res.asw_pid" +
            " INNER JOIN tbl_common_code AS cd" +
            "  ON qus.dv_code_pid = cd.code_pid" +
            "  AND cd.del_at = 'N'" +
            " WHERE req.area_nm = ?#{#inspectionForm.areaNm}" +
            " AND req.schl_nm = ?#{#inspectionForm.schlNm}" +
            " AND req.grade = ?#{#inspectionForm.grade}" +
            " AND req.ban = ?#{#inspectionForm.ban}" +
            " AND req.crs_mst_pid = ?#{#inspectionForm.crsMstPid}" +
            " AND req.mber_pid = ?#{#inspectionForm.mberPid}" +
            " GROUP BY cd.code_nm, qus.dv_code_pid, qus.qesitm_pid, ans.answer_cnts" +
            " ORDER BY qus.qesitm_pid, ans.asw_pid"
            , nativeQuery = true)
    List<Object[]> myInspectionResult1_2(InspectionForm inspectionForm);

    @Query(value = "SELECT" +
            "  cd.code_nm" +
            "  ,qus.dv_code_pid" +
            "  ,qus.qesitm_pid" +
            "  ,qus.qestn_qesitm" +
            "  ,ans.answer_cnts" +
            "  ,SUM(case when res.asw_pid IS NULL then 0 ELSE 1 END) AS cVal" +
            " FROM tbl_course_request AS req" +
            " INNER JOIN tbl_course_request_complete AS cpl " +
            "  ON req.atnlc_req_pid = cpl.atnlc_req_pid " +
            "  AND req.crs_mst_pid = cpl.crs_mst_pid" +
            "  AND cpl.crs_pid = ?#{#inspectionForm.id}" +
            "  AND cpl.sn = ?#{#inspectionForm.sn}" +
            "  AND cpl.cmpl_stt_ty = 'COMPLETE'" +
            " INNER JOIN tbl_inspection_response_person AS per" +
            "  ON req.atnlc_req_pid = per.atnlc_req_pid" +
            "  AND per.inspct_pid = ?#{#inspectionForm.id}" +
            "  AND per.del_at = 'N'" +
            "  AND per.inspct_dv_ty = ?#{#inspectionForm.inspctDvTy}" +
            " INNER JOIN tbl_inspection AS inp" +
            "  ON per.inspct_pid = inp.inspct_pid" +
            " INNER JOIN tbl_inspection_question_item AS qus" +
            "  ON inp.inspct_pid = qus.inspct_pid" +
            "  AND qus.dv_code_pid IN (?#{#inspectionForm.dvCodePid1})" +
            "  AND qus.del_at = 'N'" +
            " INNER JOIN tbl_inspection_answer_item AS ans" +
            "  ON qus.qesitm_pid = ans.qesitm_pid" +
            "  AND ans.del_at = 'N'" +
            " left JOIN tbl_inspection_response AS res" +
            "  ON qus.qesitm_pid = res.qesitm_pid" +
            "  AND per.rsp_ps_pid = res.rsp_ps_pid" +
            "  AND ans.asw_pid = res.asw_pid" +
            " INNER JOIN tbl_common_code AS cd" +
            "  ON qus.dv_code_pid = cd.code_pid" +
            "  AND cd.del_at = 'N'" +
            " WHERE req.area_nm = ?#{#inspectionForm.areaNm}" +
            " AND req.schl_nm = ?#{#inspectionForm.schlNm}" +
            " AND req.grade = ?#{#inspectionForm.grade}" +
            " AND req.ban = ?#{#inspectionForm.ban}" +
            " AND req.crs_mst_pid = ?#{#inspectionForm.crsMstPid}" +
            " GROUP BY cd.code_nm, qus.dv_code_pid, qus.qesitm_pid, qus.qestn_qesitm, ans.answer_cnts" +
            " ORDER BY qus.qesitm_pid, ans.asw_pid"
            , nativeQuery = true)
    List<Object[]> classCourseInspectionResult1_2(InspectionForm inspectionForm);

    @Query(value = "SELECT" +
            "  t4.code_nm" +
            "  ,t4.dv_code_pid" +
            "  ,t4.mVal" +
            "  ,rst.cnts" +
            " FROM" +
            " (" +
            "  SELECT" +
            "   code_nm" +
            "   ,dv_code_pid" +
            "   ,case when mVal >= 70 then 2" +
            "    when mVal < 70 and mVal >= 30 then 1" +
            "    ELSE 0" +
            "    END AS mVal" +
            "  FROM" +
            "  (" +
            "    SELECT" +
            "     code_nm" +
            "     ,dv_code_pid" +
            "     ,sum(" +
            "      case " +
            "      when mber_pid = ?#{#inspectionForm.mberPid} then tot" +
            "      ELSE 0 " +
            "      END" +
            "     ) / (?#{#inspectionForm.dvQuestionCnt1} * 5) * 100 as mVal" +
            "     ,sum(tot) / (?#{#inspectionForm.dvQuestionCnt1} * 5 * reqCnt) * 100 AS cVal" +
            "    FROM" +
            "    (" +
            "     SELECT" +
            "       mber_pid" +
            "       ,code_nm" +
            "       ,dv_code_pid" +
            "       ,sum(case when sn is not null then sn ELSE 0 end) AS tot" +
            "       ,reqCnt" +
            "     FROM" +
            "     (" +
            "      SELECT" +
            "       req.mber_pid" +
            "       ,cd.code_nm" +
            "       ,qus.dv_code_pid" +
            "       ,ans.sn" +
            "       ,rCnt.cnt AS reqCnt" +
            "      FROM tbl_course_request AS req" +
            "      INNER JOIN tbl_course_request_complete AS cpl " +
            "       ON req.atnlc_req_pid = cpl.atnlc_req_pid " +
            "       AND req.crs_mst_pid = cpl.crs_mst_pid" +
            "       AND cpl.crs_pid = ?#{#inspectionForm.id}" +
            "       AND cpl.sn = ?#{#inspectionForm.sn}" +
            "       AND cpl.cmpl_stt_ty = 'COMPLETE'" +
            "      INNER JOIN tbl_inspection_response_person AS per" +
            "       ON req.atnlc_req_pid = per.atnlc_req_pid" +
            "       AND per.inspct_pid = ?#{#inspectionForm.id}" +
            "       AND per.del_at = 'N'" +
            "       AND per.inspct_dv_ty = ?#{#inspectionForm.inspctDvTy}" +
            "      INNER JOIN tbl_inspection AS inp" +
            "       ON per.inspct_pid = inp.inspct_pid" +
            "      INNER JOIN tbl_inspection_question_item AS qus" +
            "       ON inp.inspct_pid = qus.inspct_pid" +
            "       AND qus.dv_code_pid IN (?#{#inspectionForm.dvCodePid1})" +
            "       AND qus.del_at = 'N'" +
            "      INNER JOIN tbl_inspection_answer_item AS ans" +
            "       ON qus.qesitm_pid = ans.qesitm_pid" +
            "       AND ans.del_at = 'N'" +
            "      INNER JOIN tbl_inspection_response AS res" +
            "       ON qus.qesitm_pid = res.qesitm_pid" +
            "       AND per.rsp_ps_pid = res.rsp_ps_pid" +
            "       AND ans.asw_pid = res.asw_pid" +
            "      INNER JOIN tbl_common_code AS cd" +
            "       ON qus.dv_code_pid = cd.code_pid" +
            "       AND cd.del_at = 'N'" +
            "      INNER JOIN (SELECT area_nm, schl_nm, grade, ban, crs_mst_pid, COUNT(*) AS cnt FROM tbl_course_request GROUP BY area_nm, schl_nm, grade, ban, crs_mst_pid) AS rCnt" +
            "       ON req.area_nm = rCnt.area_nm" +
            "       AND req.schl_nm = rCnt.schl_nm" +
            "       AND req.grade = rCnt.grade" +
            "       AND req.ban = rCnt.ban" +
            "       AND req.crs_mst_pid = rCnt.crs_mst_pid" +
            "    WHERE req.area_nm = ?#{#inspectionForm.areaNm}" +
            "    AND req.schl_nm = ?#{#inspectionForm.schlNm}" +
            "    AND req.grade = ?#{#inspectionForm.grade}" +
            "    AND req.ban = ?#{#inspectionForm.ban}" +
            "    AND req.crs_mst_pid = ?#{#inspectionForm.crsMstPid}" +
            "     ) AS t" +
            "     GROUP BY mber_pid, code_nm, dv_code_pid" +
            "    ) AS t2" +
            "    GROUP BY code_nm, dv_code_pid" +
            "    ORDER BY dv_code_pid" +
            "   ) AS t3" +
            " ) AS t4" +
            " INNER JOIN tbl_inspection_result AS rst" +
            "  ON t4.dv_code_pid = rst.dv_code_pid" +
            "  AND t4.mVal = rst.sn" +
            " ORDER BY dv_code_pid"
            , nativeQuery = true)
    List<Object[]> myInspectionResultBar(InspectionForm inspectionForm);

    @Query(value = "SELECT" +
            "  code_nm" +
            "  ,dv_code_pid" +
            "  ,sum(" +
            "   case " +
            "   when mber_pid = ?#{#inspectionForm.mberPid} then tot" +
            "   ELSE 0 " +
            "   END" +
            "  ) / (?#{#inspectionForm.dvQuestionCnt1} * 5) * 100 as mVal" +
            "  ,sum(tot) / (?#{#inspectionForm.dvQuestionCnt1} * 5 * reqCnt) * 100 AS cVal" +
            " FROM" +
            " (" +
            "  SELECT" +
            "    mber_pid" +
            "    ,code_nm" +
            "    ,dv_code_pid" +
            "    ,sum(case when sn is not null then sn ELSE 0 end) AS tot" +
            "    ,reqCnt" +
            "  FROM" +
            "  (" +
            "   SELECT" +
            "    req.mber_pid" +
            "    ,cd.code_nm" +
            "    ,qus.dv_code_pid" +
            "    ,ans.sn" +
            "    ,rCnt.cnt AS reqCnt" +
            "   FROM tbl_course_request AS req" +
            "   INNER JOIN tbl_course_request_complete AS cpl " +
            "    ON req.atnlc_req_pid = cpl.atnlc_req_pid " +
            "    AND req.crs_mst_pid = cpl.crs_mst_pid" +
            "    AND cpl.crs_pid = ?#{#inspectionForm.id}" +
            "    AND cpl.sn = ?#{#inspectionForm.sn}" +
            "    AND cpl.cmpl_stt_ty = 'COMPLETE'" +
            "   INNER JOIN tbl_inspection_response_person AS per" +
            "    ON req.atnlc_req_pid = per.atnlc_req_pid" +
            "    AND per.inspct_pid = ?#{#inspectionForm.id}" +
            "    AND per.del_at = 'N'" +
            "    AND per.inspct_dv_ty = ?#{#inspectionForm.inspctDvTy}" +
            "   INNER JOIN tbl_inspection AS inp" +
            "    ON per.inspct_pid = inp.inspct_pid" +
            "   INNER JOIN tbl_inspection_question_item AS qus" +
            "    ON inp.inspct_pid = qus.inspct_pid" +
            "    AND qus.dv_code_pid IN (?#{#inspectionForm.dvCodePid1})" +
            "    AND qus.del_at = 'N'" +
            "   INNER JOIN tbl_inspection_answer_item AS ans" +
            "    ON qus.qesitm_pid = ans.qesitm_pid" +
            "    AND ans.del_at = 'N'" +
            "   INNER JOIN tbl_inspection_response AS res" +
            "    ON qus.qesitm_pid = res.qesitm_pid" +
            "    AND per.rsp_ps_pid = res.rsp_ps_pid" +
            "    AND ans.asw_pid = res.asw_pid" +
            "   INNER JOIN tbl_common_code AS cd" +
            "    ON qus.dv_code_pid = cd.code_pid" +
            "    AND cd.del_at = 'N'" +
            "   INNER JOIN (SELECT area_nm, schl_nm, grade, ban, crs_mst_pid, COUNT(*) AS cnt FROM tbl_course_request GROUP BY area_nm, schl_nm, grade, ban, crs_mst_pid) AS rCnt" +
            "    ON req.area_nm = rCnt.area_nm" +
            "    AND req.schl_nm = rCnt.schl_nm" +
            "    AND req.grade = rCnt.grade" +
            "    AND req.ban = rCnt.ban" +
            "    AND req.crs_mst_pid = rCnt.crs_mst_pid" +
            " WHERE req.area_nm = ?#{#inspectionForm.areaNm}" +
            " AND req.schl_nm = ?#{#inspectionForm.schlNm}" +
            " AND req.grade = ?#{#inspectionForm.grade}" +
            " AND req.ban = ?#{#inspectionForm.ban}" +
            " AND req.crs_mst_pid = ?#{#inspectionForm.crsMstPid}" +
            "  ) AS t" +
            "  GROUP BY mber_pid, code_nm, dv_code_pid" +
            " ) AS t2" +
            " ORDER BY dv_code_pid"
            , nativeQuery = true)
    List<Object[]> classCourseInspectionResultBar(InspectionForm inspectionForm);

    @Query(value = "SELECT" +
            "  t4.code_nm" +
            "  ,t4.dv_code_pid" +
            "  ,t4.mVal" +
            "  ,rst.cnts" +
            " FROM" +
            " (" +
            "  SELECT" +
            "   code_nm" +
            "   ,dv_code_pid" +
            "   ,case when mVal >= 70 then 2" +
            "    when mVal < 70 and mVal >= 30 then 1" +
            "    ELSE 0" +
            "    END AS mVal" +
            "  FROM" +
            "  (" +
            "    SELECT" +
            "     code_nm" +
            "     ,dv_code_pid" +
            "     ,sum(" +
            "      case " +
            "      when dv_code_pid = ?#{#inspectionForm.dvCodePid1} then tot / (?#{#inspectionForm.dvQuestionCnt1} * 5) * 100 " +
            "      when dv_code_pid = ?#{#inspectionForm.dvCodePid2} then tot / (?#{#inspectionForm.dvQuestionCnt2} * 5) * 100" +
            "      when dv_code_pid = ?#{#inspectionForm.dvCodePid3} then tot / (?#{#inspectionForm.dvQuestionCnt3} * 5) * 100" +
            "      ELSE 0 " +
            "      END" +
            "     ) as mVal" +
            "    FROM" +
            "    (" +
            "     SELECT" +
            "       mber_pid" +
            "       ,code_nm" +
            "       ,dv_code_pid" +
            "       ,sum(case when sn is not null then sn ELSE 0 end) AS tot" +
            "       ,reqCnt" +
            "     FROM" +
            "     (" +
            "      SELECT" +
            "       req.mber_pid" +
            "       ,cd.code_nm" +
            "       ,qus.dv_code_pid" +
            "       ,ans.sn" +
            "       ,rCnt.cnt AS reqCnt" +
            "      FROM tbl_course_request AS req" +
            "      INNER JOIN tbl_course_request_complete AS cpl " +
            "       ON req.atnlc_req_pid = cpl.atnlc_req_pid " +
            "       AND req.crs_mst_pid = cpl.crs_mst_pid" +
            "       AND cpl.crs_pid = ?#{#inspectionForm.id}" +
            "       AND cpl.sn = ?#{#inspectionForm.sn}" +
            "       AND cpl.cmpl_stt_ty = 'COMPLETE'" +
            "      INNER JOIN tbl_inspection_response_person AS per" +
            "       ON req.atnlc_req_pid = per.atnlc_req_pid" +
            "       AND per.inspct_pid = ?#{#inspectionForm.id}" +
            "       AND per.del_at = 'N'" +
            "       AND per.inspct_dv_ty = ?#{#inspectionForm.inspctDvTy}" +
            "      INNER JOIN tbl_inspection AS inp" +
            "       ON per.inspct_pid = inp.inspct_pid" +
            "      INNER JOIN tbl_inspection_question_item AS qus" +
            "       ON inp.inspct_pid = qus.inspct_pid" +
            "       AND qus.dv_code_pid IN (?#{#inspectionForm.dvCodePid1}, ?#{#inspectionForm.dvCodePid2}, ?#{#inspectionForm.dvCodePid3})" +
            "       AND qus.del_at = 'N'" +
            "      INNER JOIN tbl_inspection_answer_item AS ans" +
            "       ON qus.qesitm_pid = ans.qesitm_pid" +
            "       AND ans.del_at = 'N'" +
            "      INNER JOIN tbl_inspection_response AS res" +
            "       ON qus.qesitm_pid = res.qesitm_pid" +
            "       AND per.rsp_ps_pid = res.rsp_ps_pid" +
            "       AND ans.asw_pid = res.asw_pid" +
            "      INNER JOIN tbl_common_code AS cd" +
            "       ON qus.dv_code_pid = cd.code_pid" +
            "       AND cd.del_at = 'N'" +
            "      INNER JOIN (SELECT area_nm, schl_nm, grade, ban, crs_mst_pid, COUNT(*) AS cnt FROM tbl_course_request GROUP BY area_nm, schl_nm, grade, ban, crs_mst_pid) AS rCnt" +
            "       ON req.area_nm = rCnt.area_nm" +
            "       AND req.schl_nm = rCnt.schl_nm" +
            "       AND req.grade = rCnt.grade" +
            "       AND req.ban = rCnt.ban" +
            "       AND req.crs_mst_pid = rCnt.crs_mst_pid" +
            "    WHERE req.area_nm = ?#{#inspectionForm.areaNm}" +
            "    AND req.schl_nm = ?#{#inspectionForm.schlNm}" +
            "    AND req.grade = ?#{#inspectionForm.grade}" +
            "    AND req.ban = ?#{#inspectionForm.ban}" +
            "    AND req.crs_mst_pid = ?#{#inspectionForm.crsMstPid}" +
            "    AND req.mber_pid = ?#{#inspectionForm.mberPid}" +
            "     ) AS t" +
            "     GROUP BY mber_pid, code_nm, dv_code_pid" +
            "    ) AS t2" +
            "    GROUP BY mber_pid, code_nm, dv_code_pid" +
            "    ORDER BY dv_code_pid" +
            "   ) AS t3" +
            " ) AS t4" +
            " INNER JOIN tbl_inspection_result AS rst" +
            "  ON t4.dv_code_pid = rst.dv_code_pid" +
            "  AND t4.mVal = rst.sn" +
            " ORDER BY dv_code_pid"
            , nativeQuery = true)
    List<Object[]> myInspectionResultBarMulti3(InspectionForm inspectionForm);

    @Query(value = "SELECT" +
            "  code_nm" +
            "  ,dv_code_pid" +
            "  ,sum(" +
            "   case " +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid1} AND mber_pid = ?#{#inspectionForm.mberPid} then tot / (?#{#inspectionForm.dvQuestionCnt1} * 5) * 100 " +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid2} AND mber_pid = ?#{#inspectionForm.mberPid} then tot / (?#{#inspectionForm.dvQuestionCnt2} * 5) * 100" +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid3} AND mber_pid = ?#{#inspectionForm.mberPid} then tot / (?#{#inspectionForm.dvQuestionCnt3} * 5) * 100" +
            "   ELSE 0 " +
            "   END" +
            "  ) as mVal" +
            "  ,avg(" +
            "   case " +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid1} then tot / (?#{#inspectionForm.dvQuestionCnt1} * 5) * 100 " +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid2} then tot / (?#{#inspectionForm.dvQuestionCnt2} * 5) * 100" +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid3} then tot / (?#{#inspectionForm.dvQuestionCnt3} * 5) * 100" +
            "   ELSE 0 " +
            "   END" +
            "  ) as cVal" +
            " FROM" +
            " (" +
            "  SELECT" +
            "    mber_pid" +
            "    ,code_nm" +
            "    ,dv_code_pid" +
            "    ,sum(case when sn is not null then sn ELSE 0 end) AS tot" +
            "    ,reqCnt" +
            "  FROM" +
            "  (" +
            "   SELECT" +
            "    req.mber_pid" +
            "    ,cd.code_nm" +
            "    ,qus.dv_code_pid" +
            "    ,ans.sn" +
            "    ,rCnt.cnt AS reqCnt" +
            "   FROM tbl_course_request AS req" +
            "   INNER JOIN tbl_course_request_complete AS cpl " +
            "    ON req.atnlc_req_pid = cpl.atnlc_req_pid " +
            "    AND req.crs_mst_pid = cpl.crs_mst_pid" +
            "    AND cpl.crs_pid = ?#{#inspectionForm.id}" +
            "    AND cpl.sn = ?#{#inspectionForm.sn}" +
            "    AND cpl.cmpl_stt_ty = 'COMPLETE'" +
            "   INNER JOIN tbl_inspection_response_person AS per" +
            "    ON req.atnlc_req_pid = per.atnlc_req_pid" +
            "    AND per.inspct_pid = ?#{#inspectionForm.id}" +
            "    AND per.del_at = 'N'" +
            "    AND per.inspct_dv_ty = ?#{#inspectionForm.inspctDvTy}" +
            "   INNER JOIN tbl_inspection AS inp" +
            "    ON per.inspct_pid = inp.inspct_pid" +
            "   INNER JOIN tbl_inspection_question_item AS qus" +
            "    ON inp.inspct_pid = qus.inspct_pid" +
            "    AND qus.dv_code_pid IN (?#{#inspectionForm.dvCodePid1}, ?#{#inspectionForm.dvCodePid2}, ?#{#inspectionForm.dvCodePid3})" +
            "    AND qus.del_at = 'N'" +
            "   INNER JOIN tbl_inspection_answer_item AS ans" +
            "    ON qus.qesitm_pid = ans.qesitm_pid" +
            "    AND ans.del_at = 'N'" +
            "   INNER JOIN tbl_inspection_response AS res" +
            "    ON qus.qesitm_pid = res.qesitm_pid" +
            "    AND per.rsp_ps_pid = res.rsp_ps_pid" +
            "    AND ans.asw_pid = res.asw_pid" +
            "   INNER JOIN tbl_common_code AS cd" +
            "    ON qus.dv_code_pid = cd.code_pid" +
            "    AND cd.del_at = 'N'" +
            "   INNER JOIN (SELECT area_nm, schl_nm, grade, ban, crs_mst_pid, COUNT(*) AS cnt FROM tbl_course_request GROUP BY area_nm, schl_nm, grade, ban, crs_mst_pid) AS rCnt" +
            "    ON req.area_nm = rCnt.area_nm" +
            "    AND req.schl_nm = rCnt.schl_nm" +
            "    AND req.grade = rCnt.grade" +
            "    AND req.ban = rCnt.ban" +
            "    AND req.crs_mst_pid = rCnt.crs_mst_pid" +
            " WHERE req.area_nm = ?#{#inspectionForm.areaNm}" +
            " AND req.schl_nm = ?#{#inspectionForm.schlNm}" +
            " AND req.grade = ?#{#inspectionForm.grade}" +
            " AND req.ban = ?#{#inspectionForm.ban}" +
            " AND req.crs_mst_pid = ?#{#inspectionForm.crsMstPid}" +
            "  ) AS t" +
            "  GROUP BY mber_pid, code_nm, dv_code_pid" +
            " ) AS t2" +
            " GROUP BY code_nm, dv_code_pid" +
            " ORDER BY dv_code_pid"
            , nativeQuery = true)
    List<Object[]> classCourseInspectionResultBarMulti3(InspectionForm inspectionForm);

    @Query(value = "SELECT" +
            "  t4.code_nm" +
            "  ,t4.dv_code_pid" +
            "  ,t4.mVal" +
            "  ,rst.cnts" +
            " FROM" +
            " (" +
            "  SELECT" +
            "   code_nm" +
            "   ,dv_code_pid" +
            "   ,case when mVal >= 70 then 2" +
            "    when mVal < 70 and mVal >= 30 then 1" +
            "    ELSE 0" +
            "    END AS mVal" +
            "  FROM" +
            "  (" +
            "    SELECT" +
            "     code_nm" +
            "     ,dv_code_pid" +
            "     ,sum(" +
            "      case " +
            "      when dv_code_pid = ?#{#inspectionForm.dvCodePid1} then tot / (?#{#inspectionForm.dvQuestionCnt1} * 5) * 100 " +
            "      when dv_code_pid = ?#{#inspectionForm.dvCodePid2} then tot / (?#{#inspectionForm.dvQuestionCnt2} * 5) * 100" +
            "      when dv_code_pid = ?#{#inspectionForm.dvCodePid3} then tot / (?#{#inspectionForm.dvQuestionCnt3} * 5) * 100" +
            "      when dv_code_pid = ?#{#inspectionForm.dvCodePid4} then tot / (?#{#inspectionForm.dvQuestionCnt4} * 5) * 100" +
            "      when dv_code_pid = ?#{#inspectionForm.dvCodePid5} then tot / (?#{#inspectionForm.dvQuestionCnt5} * 5) * 100" +
            "      when dv_code_pid = ?#{#inspectionForm.dvCodePid6} then tot / (?#{#inspectionForm.dvQuestionCnt6} * 5) * 100" +
            "      ELSE 0 " +
            "      END" +
            "     ) as mVal" +
            "    FROM" +
            "    (" +
            "     SELECT" +
            "       mber_pid" +
            "       ,code_nm" +
            "       ,dv_code_pid" +
            "       ,sum(case when sn is not null then sn ELSE 0 end) AS tot" +
            "       ,reqCnt" +
            "     FROM" +
            "     (" +
            "      SELECT" +
            "       req.mber_pid" +
            "       ,cd.code_nm" +
            "       ,qus.dv_code_pid" +
            "       ,ans.sn" +
            "       ,rCnt.cnt AS reqCnt" +
            "      FROM tbl_course_request AS req" +
            "      INNER JOIN tbl_course_request_complete AS cpl " +
            "       ON req.atnlc_req_pid = cpl.atnlc_req_pid " +
            "       AND req.crs_mst_pid = cpl.crs_mst_pid" +
            "       AND cpl.crs_pid = ?#{#inspectionForm.id}" +
            "       AND cpl.sn = ?#{#inspectionForm.sn}" +
            "       AND cpl.cmpl_stt_ty = 'COMPLETE'" +
            "      INNER JOIN tbl_inspection_response_person AS per" +
            "       ON req.atnlc_req_pid = per.atnlc_req_pid" +
            "       AND per.inspct_pid = ?#{#inspectionForm.id}" +
            "       AND per.del_at = 'N'" +
            "       AND per.inspct_dv_ty = ?#{#inspectionForm.inspctDvTy}" +
            "      INNER JOIN tbl_inspection AS inp" +
            "       ON per.inspct_pid = inp.inspct_pid" +
            "      INNER JOIN tbl_inspection_question_item AS qus" +
            "       ON inp.inspct_pid = qus.inspct_pid" +
            "       AND qus.dv_code_pid IN (?#{#inspectionForm.dvCodePid1}, ?#{#inspectionForm.dvCodePid2}, ?#{#inspectionForm.dvCodePid3}, ?#{#inspectionForm.dvCodePid4}, ?#{#inspectionForm.dvCodePid5}, ?#{#inspectionForm.dvCodePid6})" +
            "       AND qus.del_at = 'N'" +
            "      INNER JOIN tbl_inspection_answer_item AS ans" +
            "       ON qus.qesitm_pid = ans.qesitm_pid" +
            "       AND ans.del_at = 'N'" +
            "      INNER JOIN tbl_inspection_response AS res" +
            "       ON qus.qesitm_pid = res.qesitm_pid" +
            "       AND per.rsp_ps_pid = res.rsp_ps_pid" +
            "       AND ans.asw_pid = res.asw_pid" +
            "      INNER JOIN tbl_common_code AS cd" +
            "       ON qus.dv_code_pid = cd.code_pid" +
            "       AND cd.del_at = 'N'" +
            "      INNER JOIN (SELECT area_nm, schl_nm, grade, ban, crs_mst_pid, COUNT(*) AS cnt FROM tbl_course_request GROUP BY area_nm, schl_nm, grade, ban, crs_mst_pid) AS rCnt" +
            "       ON req.area_nm = rCnt.area_nm" +
            "       AND req.schl_nm = rCnt.schl_nm" +
            "       AND req.grade = rCnt.grade" +
            "       AND req.ban = rCnt.ban" +
            "       AND req.crs_mst_pid = rCnt.crs_mst_pid" +
            "    WHERE req.area_nm = ?#{#inspectionForm.areaNm}" +
            "    AND req.schl_nm = ?#{#inspectionForm.schlNm}" +
            "    AND req.grade = ?#{#inspectionForm.grade}" +
            "    AND req.ban = ?#{#inspectionForm.ban}" +
            "    AND req.crs_mst_pid = ?#{#inspectionForm.crsMstPid}" +
            "    AND req.mber_pid = ?#{#inspectionForm.mberPid}" +
            "     ) AS t" +
            "     GROUP BY mber_pid, code_nm, dv_code_pid" +
            "    ) AS t2" +
            "    GROUP BY mber_pid, code_nm, dv_code_pid" +
            "    ORDER BY dv_code_pid" +
            "   ) AS t3" +
            " ) AS t4" +
            " INNER JOIN tbl_inspection_result AS rst" +
            "  ON t4.dv_code_pid = rst.dv_code_pid" +
            "  AND t4.mVal = rst.sn" +
            " ORDER BY dv_code_pid"
            , nativeQuery = true)
    List<Object[]> myInspectionResultBarMulti6(InspectionForm inspectionForm);

    @Query(value = "SELECT" +
            "  code_nm" +
            "  ,dv_code_pid" +
            "  ,CONVERT(sum(" +
            "   case " +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid1} AND mber_pid = ?#{#inspectionForm.mberPid} then tot / (?#{#inspectionForm.dvQuestionCnt1} * 5) * 100 " +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid2} AND mber_pid = ?#{#inspectionForm.mberPid} then tot / (?#{#inspectionForm.dvQuestionCnt2} * 5) * 100" +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid3} AND mber_pid = ?#{#inspectionForm.mberPid} then tot / (?#{#inspectionForm.dvQuestionCnt3} * 5) * 100" +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid4} AND mber_pid = ?#{#inspectionForm.mberPid} then tot / (?#{#inspectionForm.dvQuestionCnt4} * 5) * 100" +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid5} AND mber_pid = ?#{#inspectionForm.mberPid} then tot / (?#{#inspectionForm.dvQuestionCnt5} * 5) * 100" +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid6} AND mber_pid = ?#{#inspectionForm.mberPid} then tot / (?#{#inspectionForm.dvQuestionCnt6} * 5) * 100" +
            "   ELSE 0 " +
            "   END" +
            "  ), DECIMAL(10, 2)) as mVal" +
            "  ,CONVERT(avg(" +
            "   case " +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid1} then tot / (?#{#inspectionForm.dvQuestionCnt1} * 5) * 100 " +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid2} then tot / (?#{#inspectionForm.dvQuestionCnt2} * 5) * 100" +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid3} then tot / (?#{#inspectionForm.dvQuestionCnt3} * 5) * 100" +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid4} then tot / (?#{#inspectionForm.dvQuestionCnt4} * 5) * 100" +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid5} then tot / (?#{#inspectionForm.dvQuestionCnt5} * 5) * 100" +
            "   when dv_code_pid = ?#{#inspectionForm.dvCodePid6} then tot / (?#{#inspectionForm.dvQuestionCnt6} * 5) * 100" +
            "   ELSE 0 " +
            "   END" +
            "  ), DECIMAL(10, 2)) as cVal" +
            " FROM" +
            " (" +
            "  SELECT" +
            "    mber_pid" +
            "    ,code_nm" +
            "    ,dv_code_pid" +
            "    ,sum(case when sn is not null then sn ELSE 0 end) AS tot" +
            "    ,reqCnt" +
            "  FROM" +
            "  (" +
            "   SELECT" +
            "    req.mber_pid" +
            "    ,cd.code_nm" +
            "    ,qus.dv_code_pid" +
            "    ,ans.sn" +
            "    ,rCnt.cnt AS reqCnt" +
            "   FROM tbl_course_request AS req" +
            "   INNER JOIN tbl_course_request_complete AS cpl " +
            "    ON req.atnlc_req_pid = cpl.atnlc_req_pid " +
            "    AND req.crs_mst_pid = cpl.crs_mst_pid" +
            "    AND cpl.crs_pid = ?#{#inspectionForm.id}" +
            "    AND cpl.sn = ?#{#inspectionForm.sn}" +
            "    AND cpl.cmpl_stt_ty = 'COMPLETE'" +
            "   INNER JOIN tbl_inspection_response_person AS per" +
            "    ON req.atnlc_req_pid = per.atnlc_req_pid" +
            "    AND per.inspct_pid = ?#{#inspectionForm.id}" +
            "    AND per.del_at = 'N'" +
            "    AND per.inspct_dv_ty = ?#{#inspectionForm.inspctDvTy}" +
            "   INNER JOIN tbl_inspection AS inp" +
            "    ON per.inspct_pid = inp.inspct_pid" +
            "   INNER JOIN tbl_inspection_question_item AS qus" +
            "    ON inp.inspct_pid = qus.inspct_pid" +
            "    AND qus.dv_code_pid IN (?#{#inspectionForm.dvCodePid1}, ?#{#inspectionForm.dvCodePid2}, ?#{#inspectionForm.dvCodePid3}, ?#{#inspectionForm.dvCodePid4}, ?#{#inspectionForm.dvCodePid5}, ?#{#inspectionForm.dvCodePid6})" +
            "    AND qus.del_at = 'N'" +
            "   INNER JOIN tbl_inspection_answer_item AS ans" +
            "    ON qus.qesitm_pid = ans.qesitm_pid" +
            "    AND ans.del_at = 'N'" +
            "   INNER JOIN tbl_inspection_response AS res" +
            "    ON qus.qesitm_pid = res.qesitm_pid" +
            "    AND per.rsp_ps_pid = res.rsp_ps_pid" +
            "    AND ans.asw_pid = res.asw_pid" +
            "   INNER JOIN tbl_common_code AS cd" +
            "    ON qus.dv_code_pid = cd.code_pid" +
            "    AND cd.del_at = 'N'" +
            "   INNER JOIN (SELECT area_nm, schl_nm, grade, ban, crs_mst_pid, COUNT(*) AS cnt FROM tbl_course_request GROUP BY area_nm, schl_nm, grade, ban, crs_mst_pid) AS rCnt" +
            "    ON req.area_nm = rCnt.area_nm" +
            "    AND req.schl_nm = rCnt.schl_nm" +
            "    AND req.grade = rCnt.grade" +
            "    AND req.ban = rCnt.ban" +
            "    AND req.crs_mst_pid = rCnt.crs_mst_pid" +
            " WHERE req.area_nm = ?#{#inspectionForm.areaNm}" +
            " AND req.schl_nm = ?#{#inspectionForm.schlNm}" +
            " AND req.grade = ?#{#inspectionForm.grade}" +
            " AND req.ban = ?#{#inspectionForm.ban}" +
            " AND req.crs_mst_pid = ?#{#inspectionForm.crsMstPid}" +
            "  ) AS t" +
            "  GROUP BY mber_pid, code_nm, dv_code_pid" +
            " ) AS t2" +
            " GROUP BY code_nm, dv_code_pid" +
            " ORDER BY dv_code_pid"
            , nativeQuery = true)
    List<Object[]> classCourseInspectionResultBarMulti6(InspectionForm inspectionForm);

    @Query(value = "SELECT" +
            "  cd.code_nm" +
            "  ,qus.dv_code_pid" +
            "  ,convert(avg(ans.sn), DECIMAL(30, 2)) AS mVal" +
            " FROM tbl_course_request AS req" +
            " INNER JOIN tbl_course_request_complete AS cpl " +
            "  ON req.atnlc_req_pid = cpl.atnlc_req_pid " +
            "  AND req.crs_mst_pid = cpl.crs_mst_pid" +
            "  AND cpl.crs_pid = ?#{#inspectionForm.id}" +
            "  AND cpl.sn = ?#{#inspectionForm.sn}" +
            "  AND cpl.cmpl_stt_ty = 'COMPLETE'" +
            " INNER JOIN tbl_inspection_response_person AS per" +
            "  ON req.atnlc_req_pid = per.atnlc_req_pid" +
            "  AND per.inspct_pid = ?#{#inspectionForm.id}" +
            "  AND per.del_at = 'N'" +
            "  AND per.inspct_dv_ty = ?#{#inspectionForm.inspctDvTy}" +
            " INNER JOIN tbl_inspection AS inp" +
            "  ON per.inspct_pid = inp.inspct_pid" +
            " INNER JOIN tbl_inspection_question_item AS qus" +
            "  ON inp.inspct_pid = qus.inspct_pid" +
            "  AND qus.dv_code_pid IN (?#{#inspectionForm.dvCodePid1})" +
            "  AND qus.del_at = 'N'" +
            " INNER JOIN tbl_inspection_answer_item AS ans" +
            "  ON qus.qesitm_pid = ans.qesitm_pid" +
            "  AND ans.del_at = 'N'" +
            " left JOIN tbl_inspection_response AS res" +
            "  ON qus.qesitm_pid = res.qesitm_pid" +
            "  AND per.rsp_ps_pid = res.rsp_ps_pid" +
            "  AND ans.asw_pid = res.asw_pid" +
            " INNER JOIN tbl_common_code AS cd" +
            "  ON qus.dv_code_pid = cd.code_pid" +
            "  AND cd.del_at = 'N'" +
            " WHERE req.area_nm = ?#{#inspectionForm.areaNm}" +
            " AND req.schl_nm = ?#{#inspectionForm.schlNm}" +
            " AND req.grade = ?#{#inspectionForm.grade}" +
            " AND req.ban = ?#{#inspectionForm.ban}" +
            " AND req.crs_mst_pid = ?#{#inspectionForm.crsMstPid}" +
            " AND req.mber_pid = ?#{#inspectionForm.mberPid}" +
            " GROUP BY cd.code_nm, qus.dv_code_pid"
            , nativeQuery = true)
    List<Object[]> myInspectionResultAvg(InspectionForm inspectionForm);
}
    /*SELECT
            code_nm
            ,dv_code_pid
	,(
            case
            when dv_code_pid = 72 AND tot>=3 then 1
            when dv_code_pid = 73 AND tot>=2 then 1
            when dv_code_pid = 74 AND tot>=2 then 1
            when dv_code_pid = 75 AND tot>=1 then 1
            ELSE 0
            END
            ) as mVal
            FROM
            (
            SELECT
            mber_pid
            ,code_nm
            ,dv_code_pid
            ,sum(case when answer_cnts = '있음' then 1 ELSE 0 end) AS tot
            FROM
            (
            SELECT
            req.mber_pid
            ,cd.code_nm
            ,qus.dv_code_pid
            ,ans.answer_cnts
            FROM tbl_course_request AS req
            INNER JOIN tbl_course_request_complete AS cpl
            ON req.atnlc_req_pid = cpl.atnlc_req_pid
            AND req.crs_mst_pid = cpl.crs_mst_pid
            AND cpl.crs_pid = 5
            AND cpl.sn = 2
            AND cpl.cmpl_stt_ty = 'COMPLETE'
            INNER JOIN tbl_inspection_response_person AS per
            ON req.atnlc_req_pid = per.atnlc_req_pid
            AND per.inspct_pid = 5
            AND per.del_at = 'N'
            AND per.inspct_dv_ty = 'BEFORE'
            INNER JOIN tbl_inspection AS inp
            ON per.inspct_pid = inp.inspct_pid
            INNER JOIN tbl_inspection_question_item AS qus
            ON inp.inspct_pid = qus.inspct_pid
            AND qus.dv_code_pid IN (72, 73, 74, 75)
            AND qus.del_at = 'N'
            INNER JOIN tbl_inspection_answer_item AS ans
            ON qus.qesitm_pid = ans.qesitm_pid
            AND ans.del_at = 'N'
            INNER JOIN tbl_inspection_response AS res
            ON qus.qesitm_pid = res.qesitm_pid
            AND per.rsp_ps_pid = res.rsp_ps_pid
            AND ans.asw_pid = res.asw_pid
            INNER JOIN tbl_common_code AS cd
            ON qus.dv_code_pid = cd.code_pid
            AND cd.del_at = 'N'
            WHERE req.area_nm = '경기도'
            AND req.schl_nm = '부용고등학교'
            AND req.grade = 1
            AND req.ban = '1'
            AND req.crs_mst_pid = 12
            AND req.mber_pid = 41
            ) AS t
            GROUP BY mber_pid, code_nm, dv_code_pid
            ) AS t2
            ORDER BY dv_code_pid
            ;


            #SELECT * FROM tbl_inspection_response_person;*/


    /*SELECT
            code_nm
            ,dv_code_pid
	,sum(
            case
            when mber_pid=41 then tot
            ELSE 0
            END
            ) / 45 * 100 as mVal
            ,sum(tot) / (45*4) * 100 AS cVal
            FROM
            (
            SELECT
            mber_pid
            ,code_nm
            ,dv_code_pid
            ,sum(
            case when answer_cnts = '매우 그렇다' then 5
            when answer_cnts = '그렇다' then 4
            when answer_cnts = '보통이다' then 3
            when answer_cnts = '아니다' then 2
            when answer_cnts = '매우 아니다' then 1
            ELSE 0
            end) AS tot
            FROM
            (
            SELECT
            req.mber_pid
            ,cd.code_nm
            ,qus.dv_code_pid
            ,ans.answer_cnts
            FROM tbl_course_request AS req
            INNER JOIN tbl_course_request_complete AS cpl
            ON req.atnlc_req_pid = cpl.atnlc_req_pid
            AND req.crs_mst_pid = cpl.crs_mst_pid
            AND cpl.crs_pid = 5
            AND cpl.sn = 2
            AND cpl.cmpl_stt_ty = 'COMPLETE'
            INNER JOIN tbl_inspection_response_person AS per
            ON req.atnlc_req_pid = per.atnlc_req_pid
            AND per.inspct_pid = 5
            AND per.del_at = 'N'
            AND per.inspct_dv_ty = 'BEFORE'
            INNER JOIN tbl_inspection AS inp
            ON per.inspct_pid = inp.inspct_pid
            INNER JOIN tbl_inspection_question_item AS qus
            ON inp.inspct_pid = qus.inspct_pid
            AND qus.dv_code_pid IN (86)
            AND qus.del_at = 'N'
            INNER JOIN tbl_inspection_answer_item AS ans
            ON qus.qesitm_pid = ans.qesitm_pid
            AND ans.del_at = 'N'
            INNER JOIN tbl_inspection_response AS res
            ON qus.qesitm_pid = res.qesitm_pid
            AND per.rsp_ps_pid = res.rsp_ps_pid
            AND ans.asw_pid = res.asw_pid
            INNER JOIN tbl_common_code AS cd
            ON qus.dv_code_pid = cd.code_pid
            AND cd.del_at = 'N'
            WHERE req.area_nm = '경기도'
            AND req.schl_nm = '부용고등학교'
            AND req.grade = 1
            AND req.ban = '1'
            AND req.crs_mst_pid = 12
            ) AS t
            GROUP BY mber_pid, code_nm, dv_code_pid
            ) AS t2
            ORDER BY dv_code_pid
            ;


            #SELECT * FROM tbl_inspection_response_person;*/



/*
SELECT
        cd.code_nm
        ,qus.dv_code_pid
        ,qus.qesitm_pid
        ,ans.answer_cnts
        ,SUM(case when res.asw_pid IS NULL then 0 ELSE 1 END) AS cVal
        FROM tbl_course_request AS req
        INNER JOIN tbl_course_request_complete AS cpl
        ON req.atnlc_req_pid = cpl.atnlc_req_pid
        AND req.crs_mst_pid = cpl.crs_mst_pid
        AND cpl.crs_pid = 5
        AND cpl.sn = 2
        AND cpl.cmpl_stt_ty = 'COMPLETE'
        INNER JOIN tbl_inspection_response_person AS per
        ON req.atnlc_req_pid = per.atnlc_req_pid
        AND per.inspct_pid = 5
        AND per.del_at = 'N'
        AND per.inspct_dv_ty = 'BEFORE'
        INNER JOIN tbl_inspection AS inp
        ON per.inspct_pid = inp.inspct_pid
        INNER JOIN tbl_inspection_question_item AS qus
        ON inp.inspct_pid = qus.inspct_pid
        AND qus.dv_code_pid IN (97)
        AND qus.del_at = 'N'
        INNER JOIN tbl_inspection_answer_item AS ans
        ON qus.qesitm_pid = ans.qesitm_pid
        AND ans.del_at = 'N'
        left JOIN tbl_inspection_response AS res
        ON qus.qesitm_pid = res.qesitm_pid
        AND per.rsp_ps_pid = res.rsp_ps_pid
        AND ans.asw_pid = res.asw_pid
        INNER JOIN tbl_common_code AS cd
        ON qus.dv_code_pid = cd.code_pid
        AND cd.del_at = 'N'
        WHERE req.area_nm = '경기도'
        AND req.schl_nm = '부용고등학교'
        AND req.grade = 1
        AND req.ban = '1'
        AND req.crs_mst_pid = 12
        GROUP BY cd.code_nm, qus.dv_code_pid, qus.qesitm_pid, ans.answer_cnts
        ORDER BY qus.qesitm_pid, ans.asw_pid*/
