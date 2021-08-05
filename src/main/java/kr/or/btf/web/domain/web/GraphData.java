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
@Table(name = "tbl_graph_data")
@DynamicUpdate
public class GraphData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_pid")
    private Long id;

    @Column(name = "col_nm")
    private String colNm;

    @Column(name = "col_value1")
    private Float colValue1;
    @Column(name = "col_value2")
    private Float colValue2;
    @Column(name = "col_value3")
    private Float colValue3;
    @Column(name = "col_sno")
    private Integer colSno;

    @Column(name = "reg_ps_id")
    private String regPsId;
    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;

    @Column(name = "graph_pid")
    private Long graphPid;
}
