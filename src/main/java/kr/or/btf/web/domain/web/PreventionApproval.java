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
@Table(name = "tbl_prevention_approval")
@DynamicUpdate

public class PreventionApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pra_pid")
    private Long id;

    @Column(name = "pre_mst_pid")
    private Long preMstpid;

    @Column(name = "mber_pid")
    private Long mberPid;

    @Column(name = "hope_dtm")
    private String hopeDtm;

    @Column(name = "approval")
    private String approval;
}
