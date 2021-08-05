package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.PeerResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeerResponseRepository extends JpaRepository<PeerResponse, Long> {

    PeerResponse findByQesitmPidAndRspPsPidAndAndAreaNmAndSchlNmAndGradeAndBanAndNoAndTeacherNmAndTgtMberPid(Long qesitmPid, Long rspPsPid, String areaNm, String schlNm, Integer grade, String ban, Integer no, String teacherNm, Long tgtMberPid);
}
