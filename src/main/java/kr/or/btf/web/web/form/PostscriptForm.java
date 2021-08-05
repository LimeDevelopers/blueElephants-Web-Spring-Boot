package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter @Setter
public class PostscriptForm extends SearchForm{
    private Long id;
    private String areaNm;
    private String schlNm;
    private Integer grade;

    private String ban;
    private Integer no;
    private Long srtCodePid;
    private String ttl;
    private String cn;

    private String exprnCn;
    private String cntntsUrl;
    private String imgFl;
    private String regPsId;

    private LocalDateTime regDtm;
    private String updPsId;
    private LocalDateTime updDtm;
    private String delAt;

    private Long srtCodeParentPid;

}
