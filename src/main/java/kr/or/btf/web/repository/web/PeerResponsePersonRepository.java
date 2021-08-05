package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.PeerResponsePerson;
import kr.or.btf.web.domain.web.PeerResponsePersonKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeerResponsePersonRepository extends JpaRepository<PeerResponsePerson, PeerResponsePersonKey> {

    /*@PersistenceContext
    private EntityManager em;*/

    /*public void save(PeerResponsePerson form) {

        String qry = "insert into tbl_peer_response_person values (" + form.getId() + "," + form.getAreaNm() + "," + form.getSchlNm() + "," + form.getGrade() + "," + form.getBan() + "," + form.getNo() + "," + form.getTeacherNm() + "," + form.getRegPsId() + "," + form.getRegDtm() + "," + form.getUpdPsId() + "," + form.getUpdDtm() + "," + form.getDelAt() + "," + form.getPeerPid() + "," + form.getMberPid() + ")";
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();
    }*/

    /*public PeerResponsePerson findById(Long id) {
        String qry = "select * from tbl_peer_response_person where rsp_ps_pid =" + id;
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();
    }*/

    /*public void deleteByAdvcReqPid(Long advcReqPid) {
        String qry = "delete from tbl_advice_request_type where advc_req_pid =" + advcReqPid;
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();
    }*/

}
