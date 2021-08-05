package kr.or.btf.web.domain.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchDto {
    private Long dataId;
    private String ttl;
    private String cn;
    private LocalDateTime regDtm;
    private String menuNm;
    private String url;
    private String tag;
}
