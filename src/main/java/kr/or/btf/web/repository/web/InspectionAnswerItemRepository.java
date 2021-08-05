package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.InspectionAnswerItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InspectionAnswerItemRepository extends JpaRepository<InspectionAnswerItem, Long> {

    void deleteAllByQesitmPid(Long id);

}
