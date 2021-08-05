package kr.or.btf.web.domain.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VisitStatisticDto {
    private String deviceType;
    private String cnntDate;
    private Long cnntCount;
    private Long AndroidCnt;
    private Long iPhoneCnt;
    private Long MacintoshCnt;
    private Long WindowsCnt;
    private Long OtherCnt;
}
