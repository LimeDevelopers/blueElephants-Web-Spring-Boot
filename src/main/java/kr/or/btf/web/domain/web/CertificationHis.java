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
@Table(name = "tbl_certification_his")
@DynamicUpdate
public class CertificationHis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hst_pid")
    private Long id;

    @Column(name = "reg_ps_id")
    private String regPsId;

    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;

    @Column(name = "ctfhv_pid")
    private Long ctfhvPid;

}
