package kr.or.btf.web.domain.web;

import kr.or.btf.web.domain.web.enums.ContentsDvType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_course_item")
@DynamicUpdate
public class CourseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crssq_pid")
    private Long id;


    @Column(name="ttl")
    private String ttl;

    @Column(name="cn")
    private String cn;

    @Column(name="img_fl")
    private String imgFl;

    @Enumerated(EnumType.STRING)
    @Column(name = "cntnts_dv_ty")
    private ContentsDvType cntntsDvTy;

    @Column(name="cntnts_url")
    private String cntntsUrl;

    @Column(name="cntnts_len")
    private Long cntntsLen;

    @Column(name="sno")
    private int sno;

    @Column(name="crs_pid")
    private Long crsPid;

    /*@Column(name = "reg_ps_id")
    private String regPsId;
    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;
    @Column(name = "upd_ps_id")
    private String updPsId;
    @Column(name = "upd_dtm")
    private LocalDateTime updDtm;
    @Column(name = "del_at")
    private String delAt;*/

    @Transient
    private String procNm;

}
