package kr.or.btf.web.domain.web;

import kr.or.btf.web.domain.web.enums.BoardType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_board_master")
public class BoardMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mst_pid")
    private Long id;

    @Column(name = "bbs_nm")
    private String bbsNm;

    @Enumerated(EnumType.STRING)
    @Column(name = "bbs_ty")
    private BoardType bbsTy;

    @Column(name = "bbs_upend_cn")
    private String bbsUpendCn;

    @Column(name = "sntnc_head")
    private String sntncHead;

    private String pwd;

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

}
