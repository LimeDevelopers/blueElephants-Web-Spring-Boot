package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.PeerAnswerItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeerAnswerItemRepository extends JpaRepository<PeerAnswerItem, Long> {

    void deleteAllByQesitmPid(Long id);

}
