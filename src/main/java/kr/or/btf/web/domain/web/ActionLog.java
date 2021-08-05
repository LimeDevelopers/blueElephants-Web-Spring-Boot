package kr.or.btf.web.domain.web;

import kr.or.btf.web.domain.web.enums.ActionType;
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
@Table(name = "tbl_action_log")
@DynamicUpdate
public class ActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "act_pid")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "act_dv_ty")
    private ActionType actDvTy;

    @Column(name = "act_dtm")
    private LocalDateTime actDtm;

    @Column(name = "cnct_ip")
    private String cnctIp;

    @Column(name = "cnct_url")
    private String cnctUrl;

    @Column(name="mber_pid")
    private Long mberPid;
}
