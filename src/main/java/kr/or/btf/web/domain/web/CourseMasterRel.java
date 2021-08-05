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
@Table(name = "tbl_course_master_rel")
@DynamicUpdate
public class CourseMasterRel implements Serializable {

    @Id
    @Column(name = "crs_mst_pid")
    private Long crsMstPid;

    @Id
    @Column(name = "crs_pid")
    private Long crsPid;

    private Integer sn;

    @Transient
    private String pidNm;
    @Transient
    private String imgFl;
    @Transient
    private String stepTy;

}
