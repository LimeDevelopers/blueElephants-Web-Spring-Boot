package kr.or.btf.web.domain.web;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_peer_question_item")
public class PeerQuestionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qesitm_pid")
    private Long id;

    @Column(name = "asw_dv_ty")
    private String aswDvTy;

    @Column(name = "qestn_qesitm")
    private String qestnQesitm;

    @Column(name = "answer_cnt")
    private Integer answerCnt;

    @Column(name = "rspns_cnt")
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

    @Column(name = "peer_pid")
    private Long peerPid;

    @Transient
    private Long studentCnt;

    @Transient
    private Long responseCnt;

}
