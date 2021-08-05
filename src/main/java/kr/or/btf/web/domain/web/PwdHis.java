package kr.or.btf.web.domain.web;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_pwd_his")
@DynamicUpdate
public class PwdHis implements Serializable {

    @Column(name="pwd")
    private String pwd;

    @Id
    @Column(name="mber_pid")
    private Long mberPid;

    @Id
    @Column(name = "reg_dtm")
    private LocalDateTime regDtm;

}
