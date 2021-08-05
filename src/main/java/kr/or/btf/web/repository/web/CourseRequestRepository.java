package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.CourseRequest;
import kr.or.btf.web.web.form.CourseRequestForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRequestRepository extends JpaRepository<CourseRequest, Long> {

    @Query(value = "select" +
            " tcm.crs_nm" +
            " ,tcm.crs_mst_pid" +
            " ,sn2.cmpl_stt_ty as sn2SttTy" +
            " ,sn2.crs_pid as sn2CrsPid" +
            " ,sn3.cmpl_stt_ty as sn3SttTy" +
            " ,sn3.crs_pid as sn3CrsPid" +
            " ,sn4.cmpl_stt_ty as sn4SttTy" +
            " ,sn4.crs_pid as sn4CrsPid" +
            " ,sn5.cmpl_stt_ty as sn5SttTy" +
            " ,sn5.crs_pid as sn5CrsPid" +
            " ,sn6.cmpl_stt_ty as sn6SttTy" +
            " ,sn6.crs_pid as sn6CrsPid" +
            " from tbl_course_request tcr" +
            " inner join (" +
                    " select * from tbl_course_request_complete tcrc where tcrc.sn = 2" +
            " ) as sn2 on tcr.atnlc_req_pid  = sn2.atnlc_req_pid" +
            " inner join (" +
            " select * from tbl_course_request_complete tcrc where tcrc.sn = 3" +
            " ) as sn3 on tcr.atnlc_req_pid  = sn3.atnlc_req_pid" +
            " inner join (" +
            " select * from tbl_course_request_complete tcrc where tcrc.sn = 4" +
            " ) as sn4 on tcr.atnlc_req_pid  = sn4.atnlc_req_pid" +
            " inner join (" +
            " select * from tbl_course_request_complete tcrc where tcrc.sn = 5" +
            " ) as sn5 on tcr.atnlc_req_pid  = sn5.atnlc_req_pid" +
            " inner join (" +
            " select * from tbl_course_request_complete tcrc where tcrc.sn = 6" +
        " ) as sn6 on tcr.atnlc_req_pid  = sn6.atnlc_req_pid" +
        " inner join tbl_course_master as tcm on sn2.crs_mst_pid = tcm.crs_mst_pid" +
        " where tcr.mber_pid = ?#{#courseRequestForm.mberPid} and tcr.area_nm = ?#{#courseRequestForm.areaNm} and tcr.schl_nm = ?#{#courseRequestForm.schlNm} and tcr.grade = ?#{#courseRequestForm.grade} and tcr.ban = ?#{#courseRequestForm.ban}" +
        " order by tcr.reg_dtm desc", nativeQuery = true)

    List<Object[]> listForParentOrTeacher(CourseRequestForm courseRequestForm);

    CourseRequest findByIdAndMberPid(Long id, Long MberPid);

    Long countByMberPid(Long mberPid);
}
