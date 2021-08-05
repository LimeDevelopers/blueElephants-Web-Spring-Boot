package kr.or.btf.web.domain.web;

import kr.or.btf.web.domain.web.enums.MberDvType;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_board_target")
public class BoardTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tgt_pid")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "mber_dv_ty")
    private MberDvType mberDvTy;

    @Column(name = "data_pid")
    private Long dataPid;

}
