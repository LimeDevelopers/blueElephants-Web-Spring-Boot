package kr.or.btf.web.domain.web;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_statistic_log")
public class StatisticLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cnct_pid")
    private Long id;


    @Column(name = "cnct_ip")
    private String cnctIp;

    @Column(name = "cnct_dtm")
    private LocalDateTime cnctDtm;

    @Column(name = "all_cnt")
    private int allCnt;

    @Column(name = "cnct_sess")
    private String cnctSess;

    @Column(name = "cnct_eqpmn")
    private String cnctEqpmn;

    @Column(name = "refrn_url")
    private String refrnUrl;

    @Column(name = "cnct_url")
    private String cnctUrl;

}
