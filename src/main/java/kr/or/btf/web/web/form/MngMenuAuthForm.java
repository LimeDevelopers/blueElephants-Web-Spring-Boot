package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MngMenuAuthForm extends SearchForm {

    private Long menuPid;
    private Long mberPid;
    private String confmAt;

}
