package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.NamaneTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NamaneTempRepository extends JpaRepository<NamaneTemp, Long> {
    List<NamaneTemp> findAllByMberPid(Long pid);
}
