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
@Table(name = "tbl_emotional_traffic_log")
@DynamicUpdate
public class EmotionTrafficLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "etf_log_pid")
    private Long id;

    @Column(name="mber_pid")
    private Long mberPid;

    @Column(name="etf_pid")
    private Long etfPid;

    @Column(name="reg_dtm")
    private LocalDateTime regDtm;
}