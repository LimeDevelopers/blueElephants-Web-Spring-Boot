package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.CourseTaste;
import kr.or.btf.web.domain.web.FileInfo;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QCourseTaste;
import kr.or.btf.web.repository.web.CourseTasteRepository;
import kr.or.btf.web.repository.web.FileInfoRepository;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.CourseTasteForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseTasteService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final CourseTasteRepository courseTasteRepository;
    private final FileInfoRepository fileInfoRepository;
    private final ModelMapper modelMapper;

    public Page<CourseTaste> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QCourseTaste qCourseTaste = QCourseTaste.courseTaste;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qCourseTaste.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCourseTaste.delAt.eq("N"));

        QueryResults<CourseTaste> mngList = queryFactory
                .select(Projections.fields(CourseTaste.class,
                        qCourseTaste.id,
                        qCourseTaste.ttl,
                        qCourseTaste.cn,
                        qCourseTaste.imgFl,
                        qCourseTaste.cntntsUrl,
                        qCourseTaste.regPsId, qCourseTaste.regDtm, qCourseTaste.updPsId, qCourseTaste.updDtm,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qCourseTaste.regPsId)),
                                "regPsNm")

                ))
                .from(qCourseTaste)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public CourseTaste load(Long id) {
        CourseTaste courseTaste = courseTasteRepository.findById(id).orElseGet(CourseTaste::new);

        return courseTaste;
    }

    @Transactional
    public void delete(CourseTasteForm form) {
        CourseTaste courseTaste = this.load(form.getId());

        courseTaste.setUpdDtm(LocalDateTime.now());
        courseTaste.setUpdPsId(form.getUpdPsId());
        courseTaste.setDelAt(form.getDelAt());
    }

    /**
     * @param form
     * @return
     */
    @Transactional
    public boolean insert(CourseTasteForm form, MultipartFile attachImgFl) {
        try {

            if (attachImgFl.isEmpty() == false) {
                FileInfo fileInfo = FileUtilHelper.writeUploadedFile(attachImgFl, Constants.FOLDERNAME_COURSETASTE);
                if (fileInfo.getFlNm() != null) {
                    form.setImgFl(fileInfo.getFlNm());
                }
            }

            CourseTaste courseTaste = modelMapper.map(form, CourseTaste.class);
            CourseTaste save = courseTasteRepository.save(courseTaste);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean update(CourseTasteForm form, MultipartFile attachImgFl) {

        try {

            FileInfo fileInfo = new FileInfo();
            if (attachImgFl.isEmpty() == false) {
                fileInfo = FileUtilHelper.writeUploadedFile(attachImgFl, Constants.FOLDERNAME_COURSETASTE);
            }

            CourseTaste courseTaste = courseTasteRepository.findById(form.getId()).orElseGet(CourseTaste::new);
            courseTaste.setTtl(form.getTtl());
            courseTaste.setCn(form.getCn());
            if (fileInfo.getFlNm() != null) {
                courseTaste.setImgFl(fileInfo.getFlNm());
            }
            courseTaste.setCntntsDvTy(form.getCntntsDvTy());
            courseTaste.setCntntsUrl(form.getCntntsUrl());
            courseTaste.setUpdPsId(form.getUpdPsId());
            courseTaste.setUpdDtm(form.getUpdDtm());

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
