package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.MobileAuthLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileAuthLogRepository extends JpaRepository<MobileAuthLog, Long> {

    MobileAuthLog findByDmnNoAndRspNoAndMbtlnum(String dmnNo, String RspNo, String mbtlnum);
}
