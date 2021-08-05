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
@Table(name = "tbl_postscript_image")
@DynamicUpdate

public class PostscriptImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postscript_image_pid")
    private Long id;

    @Column(name = "fl_pid")
    private Long flPid;

    private String dsc;

    private Integer sn;

    @Column(name = "postscript_pid")
    private Long postscriptPid;

    @Transient
    private String flNm;
}
