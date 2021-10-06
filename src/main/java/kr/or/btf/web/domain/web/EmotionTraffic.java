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
@Table(name = "tbl_emotional_traffic")
@DynamicUpdate
public class EmotionTraffic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "etf_pid")
    private Long id;

    @Column(name="fl_pid")
    private Long flPid;

    @Column(name="etf_nm")
    private String etfNm;

    @Column(name="reward")
    private Integer reward;

    @Column(name="reg_dtm")
    private Long regDtm;
}
