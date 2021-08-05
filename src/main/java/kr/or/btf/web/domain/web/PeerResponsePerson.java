package kr.or.btf.web.domain.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_peer_response_person")
@DynamicUpdate
@IdClass(PeerResponsePersonKey.class)
public class PeerResponsePerson {

    @Id
    @Column(name = "rsp_ps_pid")
    private Long id;

    @Id
    @Column(name = "area_nm")
    private String areaNm;

    @Id
    @Column(name = "schl_nm")
    private String schlNm;

    @Id
    @Column(name = "grade")
    private Integer grade;

    @Id
    @Column(name = "ban")
    private String ban;

    @Id
    @Column(name = "no")
    private Integer no;

    @Id
    @Column(name = "teacher_nm")
    private String teacherNm;

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

    @Column(name = "mber_pid")
    private Long mberPid;

    @Transient
    private String regPsNm;
}
