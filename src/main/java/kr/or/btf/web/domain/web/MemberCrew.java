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
@Table(name = "tbl_member_crew")
@DynamicUpdate
public class MemberCrew {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_pid")
    private Long id;

    @Column(name="crew_nm")
    private String crewNm;

    @Column(name="crew_affi")
    private String crewAffi;

    @Column(name="crew_fnum")
    private int crewFNum;

    @Column(name="rpt_nm")
    private String rptNm;

    @Column(name = "attc_yn")
    private String attcYn;

    @Column(name="attc_dtm")
    private LocalDateTime attcDtm;

    @Column(name="reg_dtm")
    private LocalDateTime regDtm;

    @Column(name="mber_pid")
    private Long mberPid;

    @Transient
    private String mberNm;
    @Transient
    private String mberLoginId;
    @Transient
    private LocalDateTime mberRegDtm;
    @Transient
    private String mberMoblphon;
    @Transient
    private String mberEmail;
}
