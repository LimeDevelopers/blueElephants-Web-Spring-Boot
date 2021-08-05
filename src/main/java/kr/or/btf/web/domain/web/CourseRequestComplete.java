package kr.or.btf.web.domain.web;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_course_request_complete")
@DynamicUpdate
public class CourseRequestComplete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cmpl_hist_pid")
    private Long id;

    @Column(name = "cmpl_stt_ty")
    private String cmplSttTy;

    @Column(name = "cmpl_opetr_id")
    private String cmplOpetrId;

    @Column(name = "cmpl_prs_dtm")
    private LocalDateTime cmplPrsDtm;

    @Column(name = "atnlc_req_pid")
    private Long atnlcReqPid;

    @Column(name = "crs_mst_pid")
    private Long crsMstPid;

    @Column(name = "crs_pid")
    private Long crsPid;

    private Integer sn;

    @Transient
    private String crsNm;

}
