package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.SurveyResponsePerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyResponsePersonRepository extends JpaRepository<SurveyResponsePerson, Long> {
    SurveyResponsePerson findByLoginIdAndQustnrPid(String loginId, Long qustnrPid);
}
