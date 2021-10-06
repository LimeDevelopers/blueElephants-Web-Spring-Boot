package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.EmotionTraffic;
import kr.or.btf.web.domain.web.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmotionTrafficRepository extends JpaRepository<EmotionTraffic, Long> {
}
