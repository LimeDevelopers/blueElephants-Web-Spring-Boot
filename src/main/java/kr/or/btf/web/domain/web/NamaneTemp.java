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
@Table(name = "tbl_namane_temp")
public class NamaneTemp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "na_tp_pid")
    private Long id;

    @Column(name = "mber_pid")
    private Long mberPid;

    @Column(name = "rsp_code")
    private int rspCode;

    @Column(name = "rsp_code_msg")
    private String rspCodeMsg;

    @Column(name = "qrcode")
    private String qrcode;

    @Column(name = "qrstr")
    private String qrstr;

    @Column(name = "timg")
    private String timg;

    @Column(name = "uploadId")
    private int uploadId;

    @Column(name = "rndNo")
    private String rndNo;

    @Column(name = "myphotoType")
    private String myphotoType;

    @Column(name = "price")
    private int price;

    @Column(name = "yn")
    private String yn;

    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;
}
