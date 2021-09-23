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
@Table(name = "tbl_teammember")
@DynamicUpdate
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tmber_pid")
    private Long id;

    @Column(name = "contest_pid")
    private Long contset_pid;

    @Column(name = "app_pid")
    private Long app_pid;

    @Column(name = "t_nm")
    private String Tnm;
    @Column(name = "t_moblphon")
    private String Tmoblphon;
    @Column(name = "t_affi")
    private String Taffi;
    @Column(name = "t_brthday")
    private String Tbrthday;

    @Column(name = "t_nm1")
    private String Tnm1;
    @Column(name = "t_moblphon1")
    private String Tmoblphon1;
    @Column(name = "t_affi1")
    private String Taffi1;
    @Column(name = "t_brthday1")
    private String Tbrthday1;

    @Column(name = "t_nm2")
    private String Tnm2;
    @Column(name = "t_moblphon2")
    private String Tmoblphon2;
    @Column(name = "t_affi2")
    private String Taffi2;
    @Column(name = "t_brthday2")
    private String Tbrthday2;

    @Column(name = "t_nm3")
    private String Tnm3;
    @Column(name = "t_moblphon3")
    private String Tmoblphon3;
    @Column(name = "t_affi3")
    private String Taffi3;
    @Column(name = "t_brthday3")
    private String Tbrthday3;

    @Column(name = "t_nm4")
    private String Tnm4;
    @Column(name = "t_moblphon4")
    private String Tmoblphon4;
    @Column(name = "t_affi4")
    private String Taffi4;
    @Column(name = "t_brthday4")
    private String Tbrthday4;

    @Column(name = "t_nm5")
    private String Tnm5;
    @Column(name = "t_moblphon5")
    private String Tmoblphon5;
    @Column(name = "t_affi5")
    private String Taffi5;
    @Column(name = "t_brthday5")
    private String Tbrthday5;

}
