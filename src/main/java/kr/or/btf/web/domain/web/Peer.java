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
@Table(name = "tbl_peer")
public class Peer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "peer_pid")
    private Long id;

    private String ttl;

    private String cnts;

    @Column(name = "opn_at")
    private String opnAt;

    @Column (name = "reg_ps_id")
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
