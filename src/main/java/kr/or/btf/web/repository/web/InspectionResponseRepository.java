package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.InspectionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InspectionResponseRepository extends JpaRepository<InspectionResponse, Long> {
    void deleteByRspPsPidAndQesitmPid(Long rspPsPid, Long qesitmPid);
}
