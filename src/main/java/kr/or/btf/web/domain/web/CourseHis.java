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
@Table(name = "tbl_course_his")
@DynamicUpdate
public class CourseHis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "atnlc_pid")
    private Long id;


    @Column(name="atnlc_dtm")
    private LocalDateTime atnlcDtm;

    @Column(name="atnlc_hour")
    private int atnlcHour;

    @Column(name="cntnts_len")
    private Long cntntsLen;

    @Column(name="mber_pid")
    private Long mberPid;

    @Column(name="crssq_pid")
    private Long crssqPid;

}
