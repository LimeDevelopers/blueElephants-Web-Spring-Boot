package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.AdviceReservationTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdviceReservationTimeRepository extends JpaRepository<AdviceReservationTime, Long> {
    void deleteByAdvcRsvPid(Long advcRsvPid);
}
