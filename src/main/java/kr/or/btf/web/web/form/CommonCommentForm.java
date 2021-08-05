package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.TableNmType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommonCommentForm extends SearchForm {

    private Long id;
    private String comt;
    private Long dataPid;
    @Enumerated(EnumType.STRING)
    private TableNmType tableNm;
    private String regPsId;
    private LocalDateTime regDtm;
    private String updPsId;
    private LocalDateTime updDtm;
    private String delAt;
    private Long parntsComtPid;
}
