package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberTeacherForm extends SearchForm{

    private Long id;
    private String areaNm;
    private String schlNm;
    private Integer grade;
    private String ban;
    private Long mberPid;


}
