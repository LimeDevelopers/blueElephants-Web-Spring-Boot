package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MemberParentForm extends SearchForm{

    private Long id;
    private String stdnprntId;
    private String stdntId;
    private LocalDateTime regDtm;

}
