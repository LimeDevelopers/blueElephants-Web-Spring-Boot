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
@Table(name = "tbl_prevention")
@DynamicUpdate
/**
 * ApplicationLecture 예방교육 신청 Entity
 * @author : jerry
 * @version : 1.0.0
 * 작성일 : 2021/09/14
**/
public class Prevention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pre_pid")
    private Long id;

    @Column(name="mber_pid")
    private Long mberPid;

    @Column(name="schl_pid")
    private Long schlPid;

    @Column(name="schl_nm")
    private String schlNm;

    @Column(name="address")
    private String address;

    @Column(name="tel")
    private String tel;

    @Column(name="classes_num")
    private Integer classesNum;

    @Column(name="personnel")
    private Integer personnel;

    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;

    @Column(name = "upd_dtm")
    private LocalDateTime updDtm;

    @Column(name = "del_at")
    private String delAt;

    @Column(name = "approval")
    private String approval;
}
