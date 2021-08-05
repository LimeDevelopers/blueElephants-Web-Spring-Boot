package kr.or.btf.web.repository.web;

import kr.or.btf.web.web.form.AdviceRequestTypeForm;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
public class AdviceRequestTypeRepository {

    @PersistenceContext
    private EntityManager em;

    public void saveByCodePidAndAdvcReqPid(AdviceRequestTypeForm form) {

        String qry = "insert into tbl_advice_request_type values (" + form.getCodePid() + "," + form.getAdvcReqPid() +")";
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();
    }

    public void deleteByAdvcReqPid(Long advcReqPid) {
        String qry = "delete from tbl_advice_request_type where advc_req_pid =" + advcReqPid;
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();
    }

}
