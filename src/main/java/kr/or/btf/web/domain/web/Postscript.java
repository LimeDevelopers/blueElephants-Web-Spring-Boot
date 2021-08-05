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
@Table(name = "tbl_postscript")
@DynamicUpdate
public class Postscript {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postscript_pid")
    private Long id;

    @Column(name = "area_nm")
    private String areaNm;

    @Column(name = "schl_nm")
    private String schlNm;

    private Integer grade;

    private String ban;

    private Integer no;

    @Column(name = "srt_code_pid")
    private Long srtCodePid;

    private String ttl;

    private String cn;

    @Column(name="cntnts_url")
    private String cntntsUrl;

    @Column(name = "img_fl")
    private String imgFl;

    @Column(name = "reg_ps_id")
    private String regPsId;

    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;

    @Column(name = "upd_ps_id")
    private String updPsId;

    @Column(name = "upd_dtm")
    private LocalDateTime updDtm;

    @Column(name = "del_at")
    private String delAt;

    @Transient
    private String srtCodeNm;

    @Transient
    private String srtParentCodeNm;

    @Transient
    private Long srtParentCodePid;

    @Transient
    private String regPsNm;

}
