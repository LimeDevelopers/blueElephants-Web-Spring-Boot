package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
    void deleteByRspPsPid(Long rspPsPid);
}
