package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.MberDvType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
public class BoardTargetForm extends SearchForm {

    private Long id;

    @Enumerated(EnumType.STRING)
    private MberDvType mberDvTy;

    private Long dataPid;

}
