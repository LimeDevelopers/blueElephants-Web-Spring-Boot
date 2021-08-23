package kr.or.btf.web.domain.web;

import com.sun.org.glassfish.gmbal.Description;
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
    @Description("크루 고유번호")
    private Long id;

    @Column(name="crew_nm")
    @Description("크루 이름")
    private String crewNm;

    @Column(name="crew_affi")
    @Description("기수")
    private String crewAffi;

    @Column(name="rpt_nm")
    @Description("대표 이름")
    private String rptNm;

    @Column(name = "attc_yn")
    @Description("승인 여부")
    private String attcYn;

    @Column(name="attc_dtm")
    @Description("승인 일시")
    private Long attcDtm;

    @Column(name="reg_dtm")
    @Description("등록일")
    private LocalDateTime regDtm;

    @Column(name="mber_pid")
    @Description("멤버 고유번호")
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
