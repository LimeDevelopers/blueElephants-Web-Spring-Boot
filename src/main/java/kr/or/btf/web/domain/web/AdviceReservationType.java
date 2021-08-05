package kr.or.btf.web.domain.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_advice_reservation_type")
@DynamicUpdate
public class AdviceReservationType implements Serializable {

    @Id
    @Column(name = "code_pid")
    private Long codePid;

    @Id
    @Column(name = "advc_rsv_pid")
    private Long advcRsvPid;

    @Transient
    private String codeNm;

}
