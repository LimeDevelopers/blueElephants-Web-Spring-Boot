package kr.or.btf.web.domain.web;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_advice_reservation_time")
@DynamicUpdate
public class AdviceReservationTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rsv_hr_pid")
    private Long id;

    @Column(name="hope_time_code_id")
    private Long hopeTimeCodeId;

    @Column(name = "advc_rsv_pid")
    private Long advcRsvPid;

    @Transient
    private String hopeTimeCodeNm;

}
