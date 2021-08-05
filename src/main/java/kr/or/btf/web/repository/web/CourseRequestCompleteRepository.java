package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.CourseRequestComplete;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRequestCompleteRepository extends JpaRepository<CourseRequestComplete, Long> {

    @Query(value = "select" +
            " tmp.cmplHistPid as id" +
            " ,tmp.atnlcReqPid" +
            " ,tmp.crsMstPid" +
            " ,tmp.crsPid" +
            " ,tmp.sn" +
            " ,tmp.crsNm" +
            " ,tmp.ttl" +
            " ,tmp.crssqPid" +
            " ,tmp.procNm" +
            " ,tmp.procPer" +
            " from" +
            " (" +
                    " select" +
                    " tcrc.cmpl_hist_pid as cmplHistPid" +
                    " ,tcrc.atnlc_req_pid as atnlcReqPid" +
                    " ,tcrc.crs_mst_pid as crsMstPid" +
                    " ,tcrc.crs_pid as crsPid" +
                    " ,tcrc.sn" +
                    " ,crs.crs_nm as crsNm" +
                    " ,tci.ttl as ttl" +
                    " ,tci.crssq_pid as crssqPid" +
                    //" ,(case when ((case when his.atnlc_hour is null then 0 else his.atnlc_hour end)/(case when his.cntnts_len is null then 1 else his.cntnts_len end)) >= 1 then 'COMPLETE' else 'APPLY' end) as procNm" +
                    " ,tcrc.cmpl_stt_ty as procNm" +
                    " ,(ifnull(his.atnlc_hour, 0)/ifnull(his.cntnts_len, 1)*100) as procPer" +
                " from tbl_course_request_complete as tcrc" +
                " inner join tbl_course as crs on tcrc.crs_pid = crs.crs_pid" +
                " left join tbl_course_item as tci on tcrc.crs_pid = tci.crs_pid" +
                " left join (" +
                                " select" +
                                    " max(atnlc_hour) atnlc_hour" +
                                    " ,max(cntnts_len) cntnts_len" +
                                    " ,mber_pid" +
                                    " ,crssq_pid" +
                                " from tbl_course_his as his_one" +
                                    " group by his_one.crssq_pid, his_one.mber_pid, his_one.cntnts_len" +
                            " ) as his on tci.crssq_pid = his.crssq_pid and mber_pid = ?#{#mberPid}" +
                " where tcrc.atnlc_req_pid = ?#{#atnlcReqPid} and tcrc.sn in (3,4,5) " +

            " union all" +

                    " select" +
                    " tcrc.cmpl_hist_pid as cmplHistPid" +
                    " ,tcrc.atnlc_req_pid as atnlcReqPid" +
                    " ,tcrc.crs_mst_pid as crsMstPid" +
                    " ,tcrc.crs_pid as crsPid" +
                    " ,tcrc.sn" +
                    " ,ti.ttl as crsNm" +
                    " ,'-' as ttl" +
                    " ,0 as crssqPid" +
                    " ,tcrc.cmpl_stt_ty as procNm" +
                    " ,'-' as procPer" +
                " from tbl_course_request_complete as tcrc" +
                " inner join tbl_inspection as ti on tcrc.crs_pid = ti.inspct_pid" +
                " where tcrc.atnlc_req_pid = ?#{#atnlcReqPid} and tcrc.sn in (2,6)" +

            " union all" +

                    " select" +
                    " tcrc.cmpl_hist_pid as cmplHistPid" +
                    " ,tcrc.atnlc_req_pid as atnlcReqPid" +
                    " ,tcrc.crs_mst_pid as crsMstPid" +
                    " ,tcrc.crs_pid as crsPid" +
                    " ,tcrc.sn" +
                    " ,tct.ttl as crsNm" +
                    " ,'-' as ttl" +
                    " ,0 as crssqPid" +
                    " ,'-' as procNm" +
                    " ,'-' as procPer" +
                " from tbl_course_request_complete as tcrc" +
                " inner join tbl_course_taste as tct on tcrc.crs_pid = tct.taste_ex_pid" +
                " where tcrc.atnlc_req_pid = ?#{#atnlcReqPid} and tcrc.sn in (1)" +

            " union all" +

                    " select" +
                    " tcrc.cmpl_hist_pid as cmplHistPid" +
                    " ,tcrc.atnlc_req_pid as atnlcReqPid" +
                    " ,tcrc.crs_mst_pid as crsMstPid" +
                    " ,tcrc.crs_pid as crsPid" +
                    " ,tcrc.sn" +
                    " ,ts.ttl as crsNm" +
                    " ,'-' as ttl" +
                    " ,0 as crssqPid" +
                    " ,tcrc.cmpl_stt_ty as procNm" +
                    " ,'-' as procPer" +
                " from tbl_course_request_complete as tcrc" +
                " inner join tbl_survey as ts on tcrc.crs_pid = ts.qustnr_pid" +
                " where tcrc.atnlc_req_pid = ?#{#atnlcReqPid} and tcrc.sn in (7)" +
            " ) as tmp" +
            " order by tmp.sn asc, tmp.crsPid asc, tmp.crssqPid asc" +
            " limit ?#{#pageable.pageSize} offset ?#{#pageable.offset}",
            countQuery = "select" +
                    "     SUM(cnt)" +
                    " from" +
                    " (" +
                    " select" +
                    " COUNT(*) AS cnt" +
                    " from tbl_course_request_complete as tcrc" +
                    " inner join tbl_course as crs on tcrc.crs_pid = crs.crs_pid" +
                    " left join tbl_course_item as tci on tcrc.crs_pid = tci.crs_pid" +
                    " left join (" +
                                    " select" +
                                        " max(atnlc_hour) atnlc_hour" +
                                        " ,max(cntnts_len) cntnts_len" +
                                        " ,mber_pid" +
                                        " ,crssq_pid" +
                                    " from tbl_course_his as his_one" +
                                        " group by his_one.crssq_pid, his_one.mber_pid, his_one.cntnts_len" +
                                " ) as his on tci.crssq_pid = his.crssq_pid and mber_pid = ?#{#mberPid}" +
                    " where tcrc.atnlc_req_pid = ?#{#atnlcReqPid} and tcrc.sn in (3,4,5) " +

                    " union all" +

                    " select" +
                    " COUNT(*) AS cnt" +
                    " from tbl_course_request_complete as tcrc" +
                    " inner join tbl_inspection as ti on tcrc.crs_pid = ti.inspct_pid" +
                    " where tcrc.atnlc_req_pid = ?#{#atnlcReqPid} and tcrc.sn in (2,6)" +

                    " union all" +

                    " select" +
                    " COUNT(*) AS cnt" +
                    " from tbl_course_request_complete as tcrc" +
                    " inner join tbl_course_taste as tct on tcrc.crs_pid = tct.taste_ex_pid" +
                    " where tcrc.atnlc_req_pid = ?#{#atnlcReqPid} and tcrc.sn in (1)" +

                    " union all" +

                    " select" +
                    " COUNT(*) AS cnt" +
                    " from tbl_course_request_complete as tcrc" +
                    " inner join tbl_survey as ts on tcrc.crs_pid = ts.qustnr_pid" +
                    " where tcrc.atnlc_req_pid = ?#{#atnlcReqPid} and tcrc.sn in (7)" +
                    " ) as tmp", nativeQuery = true)

    Page<Object[]> listForMyPage(Pageable pageable, Long atnlcReqPid,Long mberPid);

    CourseRequestComplete findByAtnlcReqPidAndCrsMstPidAndCrsPidAndSn(Long atnlcReqPid, Long crsMstPid, Long crsPid, Integer sn);
}
