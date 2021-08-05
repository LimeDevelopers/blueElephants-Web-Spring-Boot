package kr.or.btf.web.domain.web;

import kr.or.btf.web.domain.web.enums.UserRollType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_experience_target")
public class ExperienceTarget implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tgt_pid")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="mber_dv_ty")
    private UserRollType mberDvTy;

    @Column(name="exprn_pid")
    private Long exprnPid;

}
