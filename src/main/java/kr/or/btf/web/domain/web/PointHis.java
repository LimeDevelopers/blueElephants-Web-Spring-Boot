package kr.or.btf.web.domain.web;

import kr.or.btf.web.domain.web.enums.PointType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_point_his")
@DynamicUpdate
public class PointHis implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pont_pid")
    private Long id;

    @Column(name = "obtain_pont")
    private Integer obtainPont;

    @Enumerated(EnumType.STRING)
    @Column(name = "pont_dv_ty")
    private PointType pontDvTy;

    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;

    @Column(name = "mber_pid")
    private Long mberPid;
}
