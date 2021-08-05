package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.ContentsDvType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
public class PlayLogForm extends SearchForm {

    private Long id;

    @Enumerated(EnumType.STRING)
    private ContentsDvType cntntsDvTy;

    private LocalDateTime actDtm;

    private String cnctUrl;

    private String cnctIp;

    private String cntntsUrl;

    private Long mberPid;

}
