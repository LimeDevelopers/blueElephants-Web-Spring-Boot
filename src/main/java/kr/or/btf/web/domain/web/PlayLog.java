package kr.or.btf.web.domain.web;

import kr.or.btf.web.domain.web.enums.ContentsDvType;
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
@Table(name = "tbl_play_log")
@DynamicUpdate
public class PlayLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cntnts_pid")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "cntnts_dv_ty")
    private ContentsDvType cntntsDvTy;

    @Column(name = "act_dtm")
    private LocalDateTime actDtm;

    @Column(name = "cnct_url")
    private String cnctUrl;

    @Column(name = "cnct_ip")
    private String cnctIp;

    @Column(name = "cntnts_url")
    private String cntntsUrl;

    @Column(name="mber_pid")
    private Long mberPid;
}
