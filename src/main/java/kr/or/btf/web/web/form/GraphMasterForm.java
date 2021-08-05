package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.GraphDvType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
public class GraphMasterForm extends SearchForm {

    private Long id;

    @Enumerated(EnumType.STRING)
    private GraphDvType graphDvTy;

    private String graphNm;

}
