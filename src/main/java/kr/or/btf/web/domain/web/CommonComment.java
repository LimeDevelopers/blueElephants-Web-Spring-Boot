package kr.or.btf.web.domain.web;

import kr.or.btf.web.domain.web.enums.TableNmType;
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
@Table(name = "tbl_common_comment")
@DynamicUpdate
public class CommonComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comt_pid")
    private Long id;


    @Column(name="comt")
    private String comt;

    @Column(name = "data_pid")
    private Long dataPid;

    @Enumerated(EnumType.STRING)
    @Column(name="table_nm")
    private TableNmType tableNm;

    @Column(name="reg_ps_id")
    private String regPsId;

    @Column(name="reg_dtm")
    private LocalDateTime regDtm;

    @Column(name = "upd_ps_id")
    private String updPsId;

    @Column(name="upd_dtm")
    private LocalDateTime updDtm;

    @Column(name = "del_at")
    private String delAt;

    @Column(name = "parnts_comt_pid")
    private Long parntsComtPid;

    @Transient
    private String regPsNm;

}
