package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.ActivityApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository  extends JpaRepository<ActivityApplication, Long> {
    @Modifying
    @Query("UPDATE ActivityApplication SET flPid = :flPid WHERE id = :id")
    int updateFlPid(Long flPid, Long id);
}
