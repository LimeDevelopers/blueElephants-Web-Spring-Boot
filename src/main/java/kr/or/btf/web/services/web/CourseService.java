package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.repository.web.CourseRepository;
import kr.or.btf.web.repository.web.FileInfoRepository;
import kr.or.btf.web.repository.web.MemberRepository;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.CourseForm;
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
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;
    private final FileInfoRepository fileInfoRepository;
    private final ModelMapper modelMapper;

    public Page<Course> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QCourse qCourse = QCourse.course;
        QCourseItem qCourseItem = QCourseItem.courseItem;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qCourse.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCourse.delAt.eq("N"));

        if (searchForm.getSrchGbn() != null && !searchForm.getSrchGbn().isEmpty()) {
            builder.and(qCourse.mberDvTy.eq(searchForm.getSrchGbn()));
        }

        if (searchForm.getSrchWord() != null && !searchForm.getSrchWord().isEmpty()) {
            builder.and(qCourse.crsNm.like("%" + searchForm.getSrchWord() + "%"));
        }

        QueryResults<Course> mngList = queryFactory
                .select(Projections.fields(Course.class,
                        qCourse.id,
                        qCourse.stepTy,
                        qCourse.mberDvTy,
                        qCourse.crsNm,
                        qCourse.crsCn,
                        qCourse.imgFl,
                        qCourse.regPsId, qCourse.regDtm, qCourse.updPsId, qCourse.updDtm,
                        ExpressionUtils.as(
                                JPAExpressions.select(qCourseItem.id.count())
                                        .from(qCourseItem)
                                        .where(qCourseItem.crsPid.eq(qCourse.id))
                                        .groupBy(qCourseItem.crsPid),
                                "courseItemCnt"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qCourse.regPsId)),
                                "regPsNm")

                ))
                .from(qCourse)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Page<Course> listByRequest(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QCourse qCourse = QCourse.course;
        QCourseItem qCourseItem = QCourseItem.courseItem;
        QCourseRequest qCourseRequest = QCourseRequest.courseRequest;
        QCommonCode qCommonCode = QCommonCode.commonCode;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qCourse.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCourse.delAt.eq("N"));

        if (searchForm.getSrchGbn() != null) {
            builder.and(qCourse.mberDvTy.eq(searchForm.getSrchGbn()));
        }

        if (searchForm.getSrchWord() != null && !searchForm.getSrchWord().isEmpty()) {
            builder.and(qCourse.crsNm.like("%" + searchForm.getSrchWord() + "%"));
        }

        QueryResults<Course> mngList = queryFactory
                .select(Projections.fields(Course.class,
                        qCourse.id,
                        qCourse.stepTy,
                        qCourse.mberDvTy,
                        qCourse.crsNm,
                        qCourse.crsCn,
                        qCourse.imgFl,
                        qCourse.regPsId, qCourse.regDtm, qCourse.updPsId, qCourse.updDtm,
                        qCourseRequest.id.as("atnlcReqPid"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCourseItem.id.count())
                                        .from(qCourseItem)
                                        .where(qCourseItem.crsPid.eq(qCourse.id))
                                        .groupBy(qCourseItem.crsPid),
                                "courseItemCnt"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qCourse.regPsId)),
                                "regPsNm")

                ))
                .from(qCourse)
                .leftJoin(qCourseRequest).on(qCourse.id.eq(qCourseRequest.crsMstPid)
                                        .and(qCourseRequest.mberPid.eq(searchForm.getUserPid())))
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Course load(Long id) {
        Course load = courseRepository.findById(id).orElseGet(Course::new);

        return load;
    }

    public Course loadByform(CourseForm form) {

        QCourse qCourse = QCourse.course;
        QCourseMasterRel qCourseMasterRel = QCourseMasterRel.courseMasterRel;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qCourse.id.eq(form.getId()));

        List<Course> list = queryFactory
                .select(Projections.fields(Course.class,
                        qCourse.id,
                        qCourse.stepTy,
                        qCourse.crsNm,
                        qCourse.crsCn,
                        qCourse.imgFl,
                        qCourse.regPsId, qCourse.regDtm, qCourse.updPsId, qCourse.updDtm,
                        qCourseMasterRel.crsMstPid
                ))
                .from(qCourse)
                .leftJoin(qCourseMasterRel).on(qCourse.id.eq(qCourseMasterRel.crsPid))
                .where(builder)
                .fetch();

        return (list != null && list.size() > 0 ? list.get(0) : new Course());
    }

    @Transactional
    public void delete(CourseForm courseForm) {
        Course mng = this.load(courseForm.getId());

        mng.setUpdDtm(LocalDateTime.now());
        mng.setUpdPsId(courseForm.getUpdPsId());
        mng.setDelAt(courseForm.getDelAt());
    }

    /**
     * @param courseForm
     * @return
     */
    @Transactional
    public Course insert(CourseForm courseForm, MultipartFile attachImgFl) throws Exception {

        if (attachImgFl.isEmpty() == false) {
            FileInfo fileInfo = FileUtilHelper.writeUploadedFile(attachImgFl, Constants.FOLDERNAME_COURSE);

            if (fileInfo.getFlNm() != null) {
                courseForm.setImgFl(fileInfo.getFlNm());
            }
        }

        Course course = modelMapper.map(courseForm, Course.class);
        Course save = courseRepository.save(course);

        return save;
    }

    @Transactional
    public boolean update(CourseForm courseForm, MultipartFile attachImgFl) {

        try {

            FileInfo fileInfo = new FileInfo();

            if (attachImgFl.isEmpty() == false) {
                fileInfo = FileUtilHelper.writeUploadedFile(attachImgFl, Constants.FOLDERNAME_COURSE);
            }

            Course course = courseRepository.findById(courseForm.getId()).orElseGet(Course::new);
            course.setStepTy(courseForm.getStepTy());
            course.setMberDvTy(courseForm.getMberDvTy());
            course.setCrsNm(courseForm.getCrsNm());
            course.setCrsCn(courseForm.getCrsCn());
            if (fileInfo.getFlNm() != null) {
                course.setImgFl(fileInfo.getFlNm());
            }

            course.setUpdPsId(courseForm.getUpdPsId());
            course.setUpdDtm(LocalDateTime.now());
            //return userRepository.save(account);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
