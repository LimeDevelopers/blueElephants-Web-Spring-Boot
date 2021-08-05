package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.AdviceAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdviceAnswerRepository extends JpaRepository<AdviceAnswer, Long> {
    AdviceAnswer findByAdvcReqPidAndDelAt(Long advcReqPid, String delAt);
}
