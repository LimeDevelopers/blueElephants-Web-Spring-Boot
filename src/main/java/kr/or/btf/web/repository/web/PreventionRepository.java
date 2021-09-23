package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.Prevention;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreventionRepository extends JpaRepository<Prevention, Long> {
    Prevention findByIdAndDelAt(Long id, String delAt);
    Prevention findByPreMstPidAndMberPid(Long id, Long mberPid);
}
