package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.PreventionInstructor;
import kr.or.btf.web.domain.web.PreventionMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreventionInstructorRepository extends JpaRepository<PreventionInstructor, Long> {
    PreventionInstructor findByIdAndMberPid(Long prePid, Long MberPid);
    PreventionInstructor findByMberPid(Long mberPid);
}
