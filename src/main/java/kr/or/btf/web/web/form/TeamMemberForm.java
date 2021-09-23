package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;


@Getter
@Setter
public class TeamMemberForm extends SearchForm {

    private Long TeamMbrid;
    private Long ContestPid;
    private Long Apppid;

    private String Tnm;
    private String Tmoblphon;
    private String Taffi;

    private String Tnm1;
    private String Tmoblphon1;
    private String Taffi1;
    private String Tbrthday1;

    private String Tnm2;
    private String Tmoblphon2;
    private String Taffi2;
    private String Tbrthday2;

    private String Tnm3;
    private String Tmoblphon3;
    private String Taffi3;
    private String Tbrthday3;

    private String Tnm4;
    private String Tmoblphon4;
    private String Taffi4;
    private String Tbrthday4;

    private String Tnm5;
    private String Tmoblphon5;
    private String Taffi5;
    private String Tbrthday5;
}
