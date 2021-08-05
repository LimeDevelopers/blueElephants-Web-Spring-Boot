package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.InspectionQuestionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InspectionQuestionItemRepository extends JpaRepository<InspectionQuestionItem, Long> {

    void deleteAllByInspctPid(Long id);

}
