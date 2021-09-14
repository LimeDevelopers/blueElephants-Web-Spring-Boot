package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class TeamMemberForm extends SearchForm {

    private Long TeamMbrid;

    private Long ContestPid;

    private Long Apppid;

    private String Nm;

    private String Moblphon;

    private String Affi;

    private int MemberArr;
}
