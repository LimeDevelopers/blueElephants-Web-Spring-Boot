package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.CommonCode;
import kr.or.btf.web.domain.web.QCommonCode;
import kr.or.btf.web.repository.web.CommonCodeRepository;
import kr.or.btf.web.web.form.CommonCodeForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommonCodeService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final ModelMapper modelMapper;
    private final CommonCodeRepository commonCodeRepository;

    public List<CommonCode> menuListForUppCdPid(Long prntCodePid) {
        QCommonCode qCommonCode = QCommonCode.commonCode;

        OrderSpecifier<Integer> orderSpecifier = qCommonCode.codeSno.asc();

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qCommonCode.prntCodePid.eq(prntCodePid));
        builder.and(qCommonCode.delAt.eq("N"));

        List<CommonCode> results = queryFactory
                .select(Projections.fields(CommonCode.class,
                        qCommonCode.id,
                        qCommonCode.prntCodePid,
                        qCommonCode.codeSno,
                        qCommonCode.codeNm,
                        qCommonCode.codeDsc,
                        qCommonCode.codeValue,
                        qCommonCode.regPsId,
                        qCommonCode.regDtm,
                        qCommonCode.updPsId,
                        qCommonCode.updDtm,
                        qCommonCode.delAt
                ))
                .from(qCommonCode)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return results;
    }

    @Transactional
    public boolean insert(CommonCodeForm form) {
        try {
            CommonCode commonCode = modelMapper.map(form, CommonCode.class);
            commonCodeRepository.save(commonCode);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean update(CommonCodeForm form) {
        try {
            CommonCode commonCode = commonCodeRepository.findById(form.getId()).get();
            commonCode.setPrntCodePid(form.getPrntCodePid());
            commonCode.setCodeSno(form.getCodeSno());
            commonCode.setCodeNm(form.getCodeNm());
            commonCode.setCodeDsc(form.getCodeDsc());
            commonCode.setCodeValue(form.getCodeValue());
            commonCode.setUpdPsId(form.getUpdPsId());
            commonCode.setUpdDtm(LocalDateTime.now());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean delete(CommonCodeForm form) {
        try {

            CommonCode commonCode = commonCodeRepository.findById(form.getId()).get();
            commonCode.setUpdPsId(form.getUpdPsId());
            commonCode.setUpdDtm(LocalDateTime.now());
            commonCode.setDelAt("Y");

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<CommonCode> getList() {
        List<CommonCode> all = commonCodeRepository.findAllByPrntCodePid(0L);

        return all;
    }

    public List<CommonCode> getSubList(Long parntsCodeId) {
        List<CommonCode> sub = commonCodeRepository.findAllByPrntCodePid(parntsCodeId);
        return sub;
    }

    public CommonCode findById(Long id) {
        return commonCodeRepository.findById(id).orElseGet(CommonCode::new);
    }

    public List<CommonCode> getCommonCodeParent(Long parntsCodeId) {
        return commonCodeRepository.findAllByPrntCodePid(parntsCodeId);
    }

    public List<CommonCode> findAll() {
        return commonCodeRepository.findAllByDelAtOrderByCodeSno("N");
    }

}
