package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
public class ExperienceTargetForm extends SearchForm {

    private Long id;

    @Enumerated(EnumType.STRING)
    private String mberDvTy;

    private Long exprnPid;

}
