package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.GraphMaster;
import kr.or.btf.web.domain.web.enums.GraphDvType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GraphMasterRepository extends JpaRepository<GraphMaster, Long> {

    GraphMaster findByGraphDvTy(GraphDvType graphDvType);
}
