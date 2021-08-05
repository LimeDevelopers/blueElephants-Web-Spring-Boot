package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.CommonCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCode, Long> {

    List<CommonCode> findAllByPrntCodePid(Long prntCodePid);

    List<CommonCode> findAllByDelAtOrderByCodeSno(String delAt);

}
