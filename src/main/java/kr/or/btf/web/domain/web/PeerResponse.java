package kr.or.btf.web.domain.web;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_peer_response")
public class PeerResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rsp_pid")
    private Long id;

    @Column(name = "asw_pid")
    private Long aswPid;

    @Column(name = "qesitm_pid")
    private Long qesitmPid;

    @Column(name = "rsp_ps_pid")
    private Long rspPsPid;

    @Column(name = "area_nm")
    private String areaNm;

    @Column(name = "schl_nm")
    private String schlNm;

    private Integer grade;

    private String ban;

    private Integer no;

    @Column(name = "teacher_nm")
    private String teacherNm;

    @Column(name = "tgt_mber_pid")
    private Long tgtMberPid;

    @Transient
    private String nm;

    @Transient
    private Integer mberNo;
    @Transient
    private String mberNm;
    @Transient
    private Integer targetNo;
    @Transient
    private String targetNm;
    @Transient
    private Integer score;

}
