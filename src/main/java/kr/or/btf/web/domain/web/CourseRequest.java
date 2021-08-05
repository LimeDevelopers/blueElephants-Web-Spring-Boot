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
@Table(name = "tbl_course_request")
@DynamicUpdate
public class CourseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "atnlc_req_pid")
    private Long id;

    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;

    @Column(name = "confm_at")
    private String confmAt;

    @Column(name = "area_nm")
    private String areaNm;

    @Column(name = "schl_nm")
    private String schlNm;

    private Integer grade;

    private String ban;

    private Integer no;

    @Column(name = "crs_mst_pid")
    private Long crsMstPid;

    @Column(name = "mber_pid")
    private Long mberPid;

    @Transient
    private Long cmplHistPid;
    @Transient
    private String mberNm;
    @Transient
    private String cmplSttTy;
    @Transient
    private String cmplOpetrId;
    @Transient
    private String cmplOpetrNm;
    @Transient
    private LocalDateTime cmplPrsDtm;
    @Transient
    private String crsNm;
    @Transient
    private Long per;
    @Transient
    private String masterCrsNm;
    @Transient
    private Integer myHour;
    @Transient
    private Long totalLen;
    @Transient
    private Integer progressSn;

}
