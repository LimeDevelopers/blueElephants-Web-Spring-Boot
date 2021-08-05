package kr.or.btf.web.domain.web;

import kr.or.btf.web.domain.web.enums.ProcessType;
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
@Table(name = "tbl_advice_answer")
@DynamicUpdate
public class AdviceAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advc_asw_pid")
    private Long id;

    @Column(name="ttl")
    private String ttl;

    @Column(name = "memo")
    private String memo;

    @Column(name="cn")
    private String cn;

    @Enumerated(EnumType.STRING)
    @Column(name="process_ty")
    private ProcessType processType;

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

    @Column(name="mber_pid")
    private Long mberPid;

    @Column(name="advc_req_pid")
    private Long advcReqPid;

    @Transient
    private String nm;

    @Transient
    private String loginId;

}
