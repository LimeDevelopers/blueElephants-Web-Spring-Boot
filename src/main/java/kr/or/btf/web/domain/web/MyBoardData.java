package kr.or.btf.web.domain.web;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_my_board_data")
public class MyBoardData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_ntt_pid")
    private Long id;

    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;

    @Column(name = "mber_pid")
    private Long mberPid;

    @Column(name = "data_pid")
    private Long dataPid;

}
