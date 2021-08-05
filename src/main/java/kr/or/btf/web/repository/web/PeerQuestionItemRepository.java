package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.PeerQuestionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeerQuestionItemRepository extends JpaRepository<PeerQuestionItem, Long> {

    void deleteAllByPeerPid(Long id);

}
