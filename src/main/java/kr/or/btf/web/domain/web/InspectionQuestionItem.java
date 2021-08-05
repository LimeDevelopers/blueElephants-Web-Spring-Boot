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
@Table(name = "tbl_inspection_question_item")
@DynamicUpdate
public class InspectionQuestionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qesitm_pid")
    private Long id;

    @Column(name="dv_code_pid")
    private Long dvCodePid;

    @Column(name="asw_dv_ty")
    private String aswDvTy;

    @Column(name="qestn_qesitm")
    private String qestnQesitm;

    @Column(name="answer_cnt")
    private Integer answerCnt;

    @Column(name="rspns_cnt")
    private Integer rspnsCnt;

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

    @Column(name="lwprt_qesitm_at")
    private String lwprtQesitmAt;

    @Column(name="inspct_pid")
    private Long inspctPid;

    @Column(name="upper_qesitm_pid")
    private Long upperQesitmPid;

    @Transient
    private String regPsNm;
    @Transient
    private String dvCodeNm;
    @Transient
    private String dvCodeFull;

}
