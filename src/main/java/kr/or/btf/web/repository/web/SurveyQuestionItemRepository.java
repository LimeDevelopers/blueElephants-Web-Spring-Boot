package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.SurveyQuestionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyQuestionItemRepository extends JpaRepository<SurveyQuestionItem, Long> {

    void deleteAllByQustnrPid(Long id);

}
