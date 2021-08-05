package kr.or.btf.web.repository.web;

import kr.or.btf.web.web.form.AdviceReservationTypeForm;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
public class AdviceReservationTypeRepository {

    @PersistenceContext
    private EntityManager em;

    public void saveByCodePidAndAdvcReqPid(AdviceReservationTypeForm form) {

        String qry = "insert into tbl_advice_reservation_type values (" + form.getCodePid() + "," + form.getAdvcRsvPid() +")";
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();
    }

    public void deleteByAdvcRsvPid(Long advcRsvPid) {
        String qry = "delete from tbl_advice_reservation_type where advc_rsv_pid =" + advcRsvPid;
        Query query = em.createNativeQuery(qry);
        query.executeUpdate();
    }

}
