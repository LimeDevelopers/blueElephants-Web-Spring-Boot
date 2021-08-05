package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PeerResponseForm extends SearchForm {

    private Long id;
    private Long aswPid;
    private Long qesitmPid;
    private Long rspPsPid;
    private String areaNm;
    private String schlNm;
    private Integer grade;
    private String ban;
    private Integer no;
    private String teacherNm;
    private Long tgtMberPid;
}
