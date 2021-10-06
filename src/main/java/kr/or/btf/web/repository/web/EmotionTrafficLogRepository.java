package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.EmotionTrafficLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmotionTrafficLogRepository  extends JpaRepository<EmotionTrafficLog, Long> {
}
