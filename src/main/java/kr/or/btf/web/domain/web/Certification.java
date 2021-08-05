package kr.or.btf.web.domain.web;

import kr.or.btf.web.web.form.SearchForm;
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
@Table(name = "tbl_certification")
@DynamicUpdate
public class Certification extends SearchForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ctfhv_pid")
    private Long id;

    @Column(name = "isu_no")
    private String isuNo;

    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;

    @Column(name = "mber_pid")
    private Long mberPid;

    @Column(name = "atnlc_req_pid")
    private Long atnlcReqPid;

    @Transient
    private String crsNm;

    @Transient
    private Long eduTm;

    @Transient
    private String nm;

    @Transient
    private String brthdy;;

}
