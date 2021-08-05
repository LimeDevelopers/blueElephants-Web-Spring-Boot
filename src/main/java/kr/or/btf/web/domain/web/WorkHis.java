package kr.or.btf.web.domain.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_work_log")
public class WorkHis implements Serializable {

    @Column(name = "table_nm")
    private String tableNm;

    @Id
    @Column(name = "opert_cn")
    private String opertCn;

    @Id
    @Column(name = "opert_dtm")
    private LocalDateTime opertDtm;

    @Column(name = "error_cd")
    private String errorCd;

    @Column(name = "error_cn")
    private String errorCn;

    @Column(name = "cnct_ip")
    private String cnctIp;

    @Column(name = "cnct_url")
    private String cnctUrl;

    @Column(name = "mber_pid")
    private Long mberPid;
}
