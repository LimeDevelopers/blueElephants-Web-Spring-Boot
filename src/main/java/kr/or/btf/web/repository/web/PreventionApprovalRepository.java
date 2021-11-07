package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.PreventionApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreventionApprovalRepository extends JpaRepository<PreventionApproval, Long> {

    Optional<PreventionApproval> findByPreMstpid(Long preMstpid);
}
