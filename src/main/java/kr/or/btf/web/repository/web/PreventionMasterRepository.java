package kr.or.btf.web.repository.web;
import kr.or.btf.web.domain.web.PreventionMaster;
import kr.or.btf.web.web.form.PreventionMasterForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PreventionMasterRepository extends JpaRepository<PreventionMaster, Long> {
    List<PreventionMaster> findByMberPid(Long id);
    PreventionMaster findByPrePidAndMberPid(Long prePid, Long MberPid);

}
