package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.SurveyAnswerItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyAnswerItemRepository extends JpaRepository<SurveyAnswerItem, Long> {

    void deleteAllByQesitmPid(Long id);

}
