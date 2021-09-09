package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.ActivityApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository  extends JpaRepository<ActivityApplication, Long> {
}
