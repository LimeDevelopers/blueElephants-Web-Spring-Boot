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
@Table(name = "tbl_inspection")
@DynamicUpdate
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inspct_pid")
    private Long id;

    @Column(name="ttl")
    private String ttl;

    @Column(name="inspct_cn")
    private String inspctCn;

    @Column(name = "mber_dv_ty")
    private String mberDvTy;

    @Column(name="opn_at")
    private String opnAt;


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
    private String regPsNm;
    @Transient
    private String mberDvTyNm;
    @Transient
    private String dvTyNm;
}
