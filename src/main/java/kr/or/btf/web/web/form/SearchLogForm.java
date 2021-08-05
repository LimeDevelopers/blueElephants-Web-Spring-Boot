package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
public class SearchLogForm extends SearchForm {

    private Long srch_pid;

    private String srchwrd;

}
