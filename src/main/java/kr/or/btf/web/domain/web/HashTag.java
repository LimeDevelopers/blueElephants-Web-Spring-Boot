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
@Table(name = "tbl_hash_tag")
public class HashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_pid")
    private Long id;

    @Column (name = "tag_nm")
    private String tagNm;

    @Column (name = "reg_dtm")
    private LocalDateTime regDtm;

    @Column (name = "data_pid")
    private Long dataPid;

    @Column (name = "table_nm")
    private String tableNm;

}
