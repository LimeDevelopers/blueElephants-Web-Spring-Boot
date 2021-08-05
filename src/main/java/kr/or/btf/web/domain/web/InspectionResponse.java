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
@Table(name = "tbl_inspection_response")
@DynamicUpdate
public class InspectionResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rsp_pid")
    private Long id;


    @Column(name="answer_cnts")
    private String answerCnts;

    @Column(name="rsp_ps_pid")
    private Long rspPsPid;

    @Column(name="asw_pid")
    private Long aswPid;

    @Column(name="qesitm_pid")
    private Long qesitmPid;

}
