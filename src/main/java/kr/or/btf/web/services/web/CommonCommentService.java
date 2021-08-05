package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.CommonComment;
import kr.or.btf.web.domain.web.FileInfo;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QCommonComment;
import kr.or.btf.web.domain.web.enums.FileDvType;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.repository.web.CommonCommentRepository;
import kr.or.btf.web.repository.web.FileInfoRepository;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.CommonCommentForm;
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
public class CommonCommentService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final CommonCommentRepository commonCommentRepository;
    private final ModelMapper modelMapper;
    private final FileInfoRepository fileInfoRepository;

    public Page<CommonComment> list(Pageable pageable, CommonCommentForm commonCommentForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QCommonComment qCommonComment = QCommonComment.commonComment;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qCommonComment.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCommonComment.delAt.eq("N"));

        if (commonCommentForm.getDataPid() != null) {
            builder.and(qCommonComment.dataPid.eq(commonCommentForm.getDataPid()));
        }

        if (commonCommentForm.getTableNm() != null) {
            builder.and(qCommonComment.tableNm.eq(commonCommentForm.getTableNm()));
        }

        QueryResults<CommonComment> mngList = queryFactory
                .select(Projections.fields(CommonComment.class,
                        qCommonComment.id,
                        qCommonComment.comt,
                        qCommonComment.tableNm,
                        qCommonComment.regPsId,
                        qCommonComment.regDtm,
                        qCommonComment.updPsId,
                        qCommonComment.updDtm,
                        qCommonComment.parntsComtPid,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qCommonComment.regPsId)
                                                .and(qAccount.delAt.eq("N"))),
                                "regPsNm")
                ))
                .from(qCommonComment)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public CommonComment loadByForm(CommonCommentForm form) {

        QCommonComment qCommonComment = QCommonComment.commonComment;
        QAccount qAccount = QAccount.account;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCommonComment.delAt.eq("N"));

        if (form.getDataPid() != null) {
            builder.and(qCommonComment.dataPid.eq(form.getDataPid()));
        }

        if (form.getTableNm() != null) {
            builder.and(qCommonComment.tableNm.eq(form.getTableNm()));
        }

        List<CommonComment> list = queryFactory
                .select(Projections.fields(CommonComment.class,
                        qCommonComment.id,
                        qCommonComment.comt,
                        qCommonComment.tableNm,
                        qCommonComment.regPsId,
                        qCommonComment.regDtm,
                        qCommonComment.updPsId,
                        qCommonComment.updDtm,
                        qCommonComment.parntsComtPid,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qCommonComment.regPsId)
                                                .and(qAccount.delAt.eq("N"))),
                                "regPsNm")
                ))
                .from(qCommonComment)
                .where(builder)
                .fetch();

        return (list != null && list.size() > 0 ? list.get(0) : new CommonComment());

    }

    public CommonComment load(Long id) {
        CommonComment commonComment = commonCommentRepository.findById(id).orElseGet(CommonComment::new);

        return commonComment;
    }

    public CommonComment loadByDuplicate(String regPsId, TableNmType tableNmType, Long dataPid, String delAt) {
        CommonComment commonComment = commonCommentRepository.findByRegPsIdAndTableNmAndDataPidAndDelAt(regPsId, tableNmType, dataPid, delAt);

        return commonComment;
    }

    public CommonComment loadByQnaAnswer(TableNmType tableNmType, Long dataPid, String delAt) {
        CommonComment commonComment = commonCommentRepository.findByTableNmAndDataPidAndDelAt(tableNmType, dataPid, delAt);

        return commonComment;
    }

    @Transactional
    public boolean delete(CommonCommentForm commonCommentForm) {
        try {
            CommonComment mng = this.load(commonCommentForm.getId());

            mng.setUpdDtm(LocalDateTime.now());
            mng.setUpdPsId(commonCommentForm.getUpdPsId());
            mng.setDelAt(commonCommentForm.getDelAt());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean insert(CommonCommentForm commonCommentForm, MultipartFile[] attachedFile) {
        try {
            CommonComment commonComment = modelMapper.map(commonCommentForm, CommonComment.class);
            CommonComment save = commonCommentRepository.save(commonComment);

            if (attachedFile != null) {
                for (MultipartFile multipartFile : attachedFile) {
                    if(multipartFile.isEmpty() == false) {
                        TableNmType tblCommonComment = TableNmType.TBL_COMMON_COMMENT;
                        FileInfo fileInfo = FileUtilHelper.writeUploadedFile(multipartFile, Constants.FOLDERNAME_COMMENT);
                        if (fileInfo != null) {
                            fileInfo.setDataPid(save.getId());
                            fileInfo.setTableNm(tblCommonComment.name());
                            fileInfo.setDvTy(FileDvType.ATTACH.name());
                            fileInfoRepository.save(fileInfo);
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean update(CommonCommentForm commonCommentForm, MultipartFile[] attachedFile) {

        try {
            CommonComment commonComment = commonCommentRepository.findById(commonCommentForm.getId()).orElseGet(CommonComment::new);
            commonComment.setComt(commonCommentForm.getComt());
            commonComment.setUpdPsId(commonCommentForm.getUpdPsId());
            commonComment.setUpdDtm(LocalDateTime.now());

            if(attachedFile != null) {
                if (attachedFile.length > 0) {
                    for (MultipartFile multipartFile : attachedFile) {
                        if(multipartFile.isEmpty() == false) {
                            TableNmType tblCommonComment = TableNmType.TBL_COMMON_COMMENT;
                            FileInfo fileInfo = FileUtilHelper.writeUploadedFile(multipartFile, Constants.FOLDERNAME_COMMENT);
                            if (fileInfo != null) {
                                fileInfo.setDataPid(commonComment.getId());
                                fileInfo.setTableNm(tblCommonComment.name());
                                fileInfo.setDvTy(FileDvType.ATTACH.name());
                                fileInfoRepository.save(fileInfo);
                            }
                        }
                    }
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean check(Long id , Long dataPid, String loginId, String delAt) {
        CommonComment commonComment = new CommonComment();
        commonComment = commonCommentRepository.findByParntsComtPidAndDataPidAndRegPsIdAndDelAt(id, dataPid, loginId ,delAt);
        Boolean result = commonComment == null ? true : false;
        return result;
    }

    public Long countByRegPsId(String regPsId) {
        return commonCommentRepository.countByRegPsIdAndDelAt(regPsId, "N");
    }
}
