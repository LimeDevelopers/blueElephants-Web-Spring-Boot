package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.AdviceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdviceRequestRepository extends JpaRepository<AdviceRequest, Long> {
    AdviceRequest findByIdAndPwd(Long id, String pwd);
}
