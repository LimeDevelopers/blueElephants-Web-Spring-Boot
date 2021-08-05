package kr.or.btf.web.domain.web;

import kr.or.btf.web.domain.web.enums.GraphDvType;
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
@Table(name = "tbl_graph_master")
@DynamicUpdate
public class GraphMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "graph_pid")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "graph_dv_ty")
    private GraphDvType graphDvTy;

    @Column(name = "graph_nm")
    private String graphNm;

    @Column(name = "reg_ps_id")
    private String regPsId;
    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;
    @Column(name = "upd_ps_id")
    private String updPsId;
    @Column(name = "upd_dtm")
    private LocalDateTime updDtm;
    @Column(name = "del_at")
    private String delAt;
}
