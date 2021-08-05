package kr.or.btf.web.domain.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AccountStatisticDto {
    private Long regularSum;
    private Long noneSum;
    private Long dormant;
    private Long withdrawalSum;
    private Long totalSum;
}
