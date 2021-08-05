package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MngMenuForm extends SearchForm{

    private Long id;
    private String menuNm;
    private String menuUrl;
    private String newwinAt;
    private Long menuGroupCdPid;
    private int menuSn;

}
