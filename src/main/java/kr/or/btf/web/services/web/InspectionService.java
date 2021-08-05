package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.Inspection;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QInspection;
import kr.or.btf.web.repository.web.InspectionRepository;
import kr.or.btf.web.web.form.InspectionForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InspectionService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final InspectionRepository inspectionRepository;
    private final ModelMapper modelMapper;

    public Page<Inspection> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QInspection qInspection = QInspection.inspection;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qInspection.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qInspection.delAt.eq("N"));

        if (searchForm.getSrchGbn() != null) {
            builder.and(qInspection.mberDvTy.eq(searchForm.getSrchGbn()));
        }

        if (searchForm.getSrchWord() != null && !searchForm.getSrchWord().isEmpty()) {
            builder.and(qInspection.ttl.like("%" + searchForm.getSrchWord() + "%"));
        }

        QueryResults<Inspection> mngList = queryFactory
                .select(Projections.fields(Inspection.class,
                        qInspection.id,
                        qInspection.ttl,
                        qInspection.inspctCn,
                        qInspection.opnAt,
                        qInspection.regPsId,
                        qInspection.regDtm,
                        qInspection.mberDvTy,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qInspection.regPsId)),
                                "regPsNm")

                ))
                .from(qInspection)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Inspection load(Long id) {
        Inspection inspection = inspectionRepository.findById(id).orElseGet(Inspection::new);

        return inspection;
    }

    public Inspection loadByform(InspectionForm form) {

        QInspection qInspection = QInspection.inspection;
        QAccount qAccount = QAccount.account;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qInspection.delAt.eq("N"));
        if (form.getId() != null) {
            builder.and(qInspection.id.eq(form.getId()));
        }

        List<Inspection> list = queryFactory
                .select(Projections.fields(Inspection.class,
                        qInspection.id,
                        qInspection.ttl,
                        qInspection.inspctCn,
                        qInspection.opnAt,
                        qInspection.regPsId,
                        qInspection.regDtm,
                        qInspection.mberDvTy,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qInspection.regPsId)),
                                "regPsNm")
                ))
                .from(qInspection)
                .where(builder)
                .fetch();

        return (list != null && list.size() > 0 ? list.get(0) : new Inspection());
    }

    @Transactional
    public void delete(InspectionForm form) {
        Inspection mng = this.load(form.getId());

        mng.setUpdDtm(LocalDateTime.now());
        mng.setUpdPsId(form.getUpdPsId());
        mng.setDelAt(form.getDelAt());
    }

    /**
     * @param form
     * @return
     */
    @Transactional
    public Inspection insert(InspectionForm form) throws Exception {

        Inspection inspection = modelMapper.map(form, Inspection.class);
        Inspection save = inspectionRepository.save(inspection);

        return save;
    }

    @Transactional
    public boolean update(InspectionForm form) {

        try {

            Inspection inspection = inspectionRepository.findById(form.getId()).orElseGet(Inspection::new);
            inspection.setTtl(form.getTtl());
            inspection.setInspctCn(form.getInspctCn());
            inspection.setMberDvTy(form.getMberDvTy());
            inspection.setOpnAt(form.getOpnAt());
            inspection.setUpdPsId(form.getUpdPsId());
            inspection.setUpdDtm(LocalDateTime.now());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public JSONArray myInspectionResult1_1(InspectionForm form) {
        try {
            List<Object[]> objects = inspectionRepository.myInspectionResult1_1(form);

            JSONArray array = new JSONArray();
            if (objects != null) {
                for (Object[] object : objects) {
                    JSONObject json = new JSONObject();
                    json.put("nm", (String) object[0]);
                    json.put("dv_code_pid", (BigInteger) object[1]);
                    json.put("mVal", (Integer) object[2]);
                    json.put("cnts", (String) object[3]);

                    array.put(json);
                }
            }

            return array;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray classCourseInspectionResult1_1(InspectionForm form) {
        try {
            List<Object[]> objects = inspectionRepository.classCourseInspectionResult1_1(form);

            JSONArray array = new JSONArray();
            if (objects != null) {
                for (Object[] object : objects) {
                    JSONObject json = new JSONObject();
                    json.put("nm", (String) object[0]);
                    json.put("mVal", (BigDecimal) object[1]);
                    json.put("cVal", (BigDecimal) object[2]);

                    array.put(json);
                }
            }

            return array;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray[] myInspectionResult1_2(InspectionForm form, Integer length) {
        try {
            List<Object[]> objects = inspectionRepository.myInspectionResult1_2(form);

            JSONArray[] array = new JSONArray[length];
            if (objects != null) {
                int idx = 0;
                BigInteger qesitm_pid = null;
                for (Object[] object : objects) {
                    if (qesitm_pid != null && !qesitm_pid.equals((BigInteger) object[2])) {
                        idx++;
                    }
                    qesitm_pid = (BigInteger) object[2];

                    JSONObject json = new JSONObject();
                    json.put("nm", (String) object[0]);
                    json.put("dv_code_pid", (BigInteger) object[1]);
                    json.put("question", (String) object[3]);
                    json.put("category", (String) object[4]);
                    json.put("cVal", (BigDecimal) object[5]);

                    if (array[idx] == null) {
                        array[idx] = new JSONArray();
                    }
                    array[idx].put(json);
                }
            }

            return array;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray[] classCourseInspectionResult1_2(InspectionForm form, Integer length) {
        try {
            List<Object[]> objects = inspectionRepository.classCourseInspectionResult1_2(form);

            JSONArray[] array = new JSONArray[length];
            if (objects != null) {
                int idx = 0;
                BigInteger qesitm_pid = null;
                for (Object[] object : objects) {
                    if (qesitm_pid != null && !qesitm_pid.equals((BigInteger) object[2])) {
                        idx++;
                    }
                    qesitm_pid = (BigInteger) object[2];

                    JSONObject json = new JSONObject();
                    json.put("nm", (String) object[0]);
                    json.put("dv_code_pid", (BigInteger) object[1]);
                    json.put("question", (String) object[3]);
                    json.put("category", (String) object[4]);
                    json.put("cVal", (BigDecimal) object[5]);

                    if (array[idx] == null) {
                        array[idx] = new JSONArray();
                    }
                    array[idx].put(json);
                }
            }

            return array;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray myInspectionResultBar(InspectionForm form) {
        try {
            List<Object[]> objects = inspectionRepository.myInspectionResultBar(form);

            JSONArray array = new JSONArray();
            if (objects != null) {
                for (Object[] object : objects) {
                    JSONObject json = new JSONObject();
                    json.put("nm", (String) object[0]);
                    json.put("dv_code_pid", (BigInteger) object[1]);
                    json.put("mVal", (Integer) object[2]);
                    json.put("cnts", (String) object[3]);

                    array.put(json);
                }
            }

            return array;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray classCourseInspectionResultBar(InspectionForm form) {
        try {
            List<Object[]> objects = inspectionRepository.classCourseInspectionResultBar(form);

            JSONArray array = new JSONArray();
            if (objects != null) {
                for (Object[] object : objects) {
                    JSONObject json = new JSONObject();
                    json.put("nm", (String) object[0]);
                    json.put("dv_code_pid", (BigInteger) object[1]);
                    json.put("mVal", (BigDecimal) object[2]);
                    json.put("cVal", (BigDecimal) object[3]);

                    array.put(json);
                }
            }

            return array;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray myInspectionResultBarMulti(InspectionForm form) {
        try {
            List<Object[]> objects = inspectionRepository.myInspectionResultBarMulti6(form);

            JSONArray array = new JSONArray();
            if (objects != null) {
                for (Object[] object : objects) {
                    JSONObject json = new JSONObject();
                    json.put("nm", (String) object[0]);
                    json.put("dv_code_pid", (BigInteger) object[1]);
                    json.put("mVal", (Integer) object[2]);
                    json.put("cnts", (String) object[3]);

                    array.put(json);
                }
            }

            return array;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray classCourseInspectionResultBarMulti(InspectionForm form) {
        try {
            List<Object[]> objects = inspectionRepository.classCourseInspectionResultBarMulti6(form);

            JSONArray array = new JSONArray();
            if (objects != null) {
                for (Object[] object : objects) {
                    JSONObject json = new JSONObject();
                    json.put("nm", (String) object[0]);
                    json.put("dv_code_pid", (BigInteger) object[1]);
                    json.put("mVal", (BigDecimal) object[2]);
                    json.put("cVal", (BigDecimal) object[3]);

                    array.put(json);
                }
            }

            return array;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray myInspectionResultAvg(InspectionForm form) {
        try {
            List<Object[]> objects = inspectionRepository.myInspectionResultAvg(form);

            JSONArray array = new JSONArray();
            if (objects != null) {
                for (Object[] object : objects) {
                    JSONObject json = new JSONObject();
                    json.put("nm", (String) object[0]);
                    json.put("dv_code_pid", (BigInteger) object[1]);
                    json.put("mVal", (BigDecimal) object[2]);

                    array.put(json);
                }
            }

            return array;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
