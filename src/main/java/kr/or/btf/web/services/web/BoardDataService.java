package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QBoardData;
import kr.or.btf.web.domain.web.QBoardTarget;
import kr.or.btf.web.domain.web.QCommonCode;
import kr.or.btf.web.domain.web.QCommonComment;
import kr.or.btf.web.domain.web.QFileDownloadHis;
import kr.or.btf.web.domain.web.QFileInfo;
import kr.or.btf.web.domain.web.QHashTag;
import kr.or.btf.web.domain.web.QMyBoardData;
import kr.or.btf.web.domain.web.dto.SearchDto;
import kr.or.btf.web.domain.web.enums.DataDvType;
import kr.or.btf.web.domain.web.enums.FileDvType;
import kr.or.btf.web.domain.web.enums.MberDvType;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.repository.web.BoardDataRepository;
import kr.or.btf.web.repository.web.BoardTargetRepository;
import kr.or.btf.web.repository.web.FileInfoRepository;
import kr.or.btf.web.repository.web.HashTagRepository;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.BoardDataForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardDataService extends _BaseService {

    @Value("${common.code.policyProposalCdPid}")
    Long policyProposalCdPid;

    private final JPAQueryFactory queryFactory;
    private final ModelMapper modelMapper;
    private final BoardDataRepository boardRepository;
    private final FileInfoRepository fileInfoRepository;
    private final BoardTargetRepository boardTargetRepository;
    private final PasswordEncoder passwordEncoder;
    private final HashTagRepository hashTagRepository;

    public Page<BoardData> list(Pageable pageable, SearchForm searchForm, BoardDataForm form) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, (searchForm.getPageSize() == null ? Constants.DEFAULT_PAGESIZE : searchForm.getPageSize())); // <- Sort 추가

        QBoardData qBoardData = QBoardData.boardData;
        QCommonCode qCommonCode = QCommonCode.commonCode;
        QAccount qAccount = QAccount.account;
        QFileInfo qFileInfo = QFileInfo.fileInfo;
        QBoardTarget qBoardTarget = QBoardTarget.boardTarget;
        QFileDownloadHis qFileDownloadHis = QFileDownloadHis.fileDownloadHis;
        QCommonComment qCommonComment = QCommonComment.commonComment;
        QMyBoardData qMyBoardData = QMyBoardData.myBoardData;

        OrderSpecifier<Long> orderSpecifier = qBoardData.id.desc();
        OrderSpecifier<String> orderSpecifierString = qBoardData.ttl.asc();
        OrderSpecifier<LocalDateTime> orderSpecifierDateTime = qBoardData.ntceDt.desc();

        NumberPath<Long> asLikeCnt = Expressions.numberPath(Long.class, "likeCnt");

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qBoardData.mstPid.eq(form.getMstPid()));
        if (form.getFixingAt() != null) {
            builder.and(qBoardData.fixingAt.eq(form.getFixingAt()));
        }
        if (form.getMstPid() != policyProposalCdPid) {
            builder.and(qBoardData.delAt.eq("N"));
        }

        if (searchForm.getLoginId() != null) {
            builder.and(qBoardData.regPsId.eq(searchForm.getLoginId()));
        }

        if (searchForm.getFixingAt() != null && !searchForm.getFixingAt().equals("")) {
            builder.and(qBoardData.fixingAt.eq(searchForm.getFixingAt()));
        }

        if (searchForm.getEduTarget() != null && !searchForm.getEduTarget().equals("")) {
            builder.and(qBoardTarget.mberDvTy.eq(MberDvType.valueOf(searchForm.getEduTarget())));
        }

        if (searchForm.getDataType() != null && !searchForm.getDataType().equals("")) {
            if (DataDvType.VIDEO.name().equals(searchForm.getDataType())) {
                builder.and(qFileInfo.flExtsn.in(FileUtilHelper.videoExt));
            } else if (DataDvType.DOCUMENT.name().equals(searchForm.getDataType())) {
                builder.and(qFileInfo.flExtsn.in(FileUtilHelper.docExt));
            } else if (DataDvType.IMAGE.name().equals(searchForm.getDataType())) {
                builder.and(qFileInfo.flExtsn.in(FileUtilHelper.imageExt));
            }
        }

        if (searchForm.getSorting() != null && !searchForm.getSorting().isEmpty()) {
            if (searchForm.getSorting().equals("like")) {
                orderSpecifier = asLikeCnt.desc();
            } else if (searchForm.getSorting().equals("readCnt")) {
                orderSpecifier = qBoardData.readCnt.longValue().desc();
            }
        }

        if (searchForm.getSrchField() != null && !searchForm.getSrchField().isEmpty()) {
            if (searchForm.getSrchField().equals("title")) {
                builder.and(qBoardData.ttl.like("%" + searchForm.getSrchWord() + "%"));
            } else if (searchForm.getSrchField().equals("cn")) {
                builder.and(qBoardData.cn.like("%" + searchForm.getSrchWord() + "%"));
            } else if (searchForm.getSrchField().equals("titleCn")) {
                builder.and(qBoardData.ttl.like("%" + searchForm.getSrchWord() + "%").or(qBoardData.cn.like("%" + searchForm.getSrchWord() + "%")));
            } else if (searchForm.getSrchField().equals("wrterNm")) {
                builder.and(qBoardData.wrterNm.like("%" + searchForm.getSrchWord() + "%"));
            }
        } else {
            if (searchForm.getSrchWord() != null && !searchForm.getSrchWord().isEmpty()) {
                builder.and(qBoardData.ttl.like("%" + searchForm.getSrchWord() + "%").or(qBoardData.cn.like("%" + searchForm.getSrchWord() + "%")));
            }
        }

        if (searchForm.getMyBoard() != null && !searchForm.getMyBoard().isEmpty()) {
            builder.and(qMyBoardData.mberPid.eq(form.getMberPid()));
        }

        if (searchForm.getSntncHead() != null && !searchForm.getSntncHead().isEmpty()) {
            builder.and(qBoardData.sntncHead.eq(searchForm.getSntncHead()));
        }

        if (searchForm.getSrchDelAt() != null && !searchForm.getSrchDelAt().isEmpty()) {
            builder.and(qBoardData.delAt.eq(searchForm.getSrchDelAt()));
        }

        JPAQuery<BoardData> tempList = queryFactory
                .select(Projections.fields(BoardData.class,
                        qBoardData.id,
                        qBoardData.thread,
                        qBoardData.depth,
                        qBoardData.sntncHead,
                        qBoardData.ttl,
                        qBoardData.cn,
                        qBoardData.wrterNm,
                        qBoardData.pwd,
                        qBoardData.readCnt,
                        qBoardData.wrterIp,
                        qBoardData.prntPwd,
                        qBoardData.regPsId,
                        qBoardData.regDtm,
                        qBoardData.updPsId,
                        qBoardData.updDtm,
                        qBoardData.ntceDt,
                        qBoardData.delAt,
                        qBoardData.fixingAt,
                        qBoardData.mstPid,
                        //qBoardTarget.mberDvTy.as("target"),
                        ExpressionUtils.as(
                                JPAExpressions.select(Expressions.stringTemplate("group_concat({0})", qBoardTarget.mberDvTy))
                                        .from(qBoardTarget)
                                        .where(qBoardTarget.dataPid.eq(qBoardData.id)),
                                "targetList"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qBoardData.regPsId)
                                                .and(qAccount.delAt.eq("N"))),
                                "regPsNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qFileInfo.flNm)
                                        .from(qFileInfo)
                                        .where(qFileInfo.dataPid.eq(qBoardData.id)
                                                .and(qFileInfo.dvTy.eq(FileDvType.THUMB.name())
                                                        .and(qFileInfo.tableNm.eq(TableNmType.TBL_BOARD_DATA.name()))))
                                        .orderBy(qFileInfo.id.desc())
                                        .offset(1L),
                                "thumbFileNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qFileInfo.id.count())
                                        .from(qFileInfo)
                                        .innerJoin(qFileDownloadHis).on(qFileInfo.id.eq(qFileDownloadHis.flPid))
                                        .where(qFileInfo.dataPid.eq(qBoardData.id)
                                                .and(qFileInfo.tableNm.eq(TableNmType.TBL_BOARD_DATA.name()))),
                                "downloadCnt"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCommonComment.updDtm)
                                        .from(qCommonComment)
                                        .where(qCommonComment.dataPid.eq(qBoardData.id)
                                                .and(qCommonComment.tableNm.eq(TableNmType.TBL_BOARD_DATA))
                                                .and(qCommonComment.delAt.eq("N")))
                                        .groupBy(qCommonComment.dataPid, qCommonComment.tableNm, qCommonComment.delAt),
                                "commentDtm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCommonComment.id.count())
                                        .from(qCommonComment)
                                        .where(qCommonComment.dataPid.eq(qBoardData.id)
                                                .and(qCommonComment.tableNm.eq(TableNmType.TBL_BOARD_DATA))
                                                .and(qCommonComment.delAt.eq("N"))),
                                asLikeCnt)
                ))
                .from(qBoardData);
        if (searchForm.getEduTarget() != null && !searchForm.getEduTarget().equals("")) {
            tempList.leftJoin(qBoardTarget).on(qBoardData.id.eq(qBoardTarget.dataPid));
        }
        if (searchForm.getDataType() != null && !searchForm.getDataType().equals("")) {
            tempList.leftJoin(qFileInfo).on(qBoardData.id.eq(qFileInfo.dataPid).and(qFileInfo.dvTy.eq(FileDvType.ATTACH.name())));
        }
        //내교육자료 leftJoin
        if (searchForm.getMyBoard() != null && !searchForm.getMyBoard().equals("")) {
            tempList.leftJoin(qMyBoardData).on(qMyBoardData.dataPid.eq(qBoardData.id));
        }
        tempList.where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());
        if (searchForm.getSorting() != null && !searchForm.getSorting().isEmpty()) {
            if (searchForm.getSorting().equals("alphabetically")) {
                tempList.orderBy(orderSpecifierString);
            }
            if (searchForm.getSorting().equals("latest")) {
                tempList.orderBy(orderSpecifierDateTime);
            }
            if (searchForm.getSorting().equals("old")) {
                orderSpecifierDateTime = qBoardData.ntceDt.asc();
                tempList.orderBy(orderSpecifierDateTime);
            }
        }
        tempList.orderBy(orderSpecifier);


        QueryResults<BoardData> mngList = tempList.fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Page<BoardData> listForFront(Pageable pageable, SearchForm searchForm, BoardDataForm form) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, (searchForm.getPageSize() == null ? Constants.DEFAULT_PAGESIZE : searchForm.getPageSize())); // <- Sort 추가

        QBoardData qBoardData = QBoardData.boardData;
        QCommonCode qCommonCode = QCommonCode.commonCode;
        QAccount qAccount = QAccount.account;
        QFileInfo qFileInfo = QFileInfo.fileInfo;
        QBoardTarget qBoardTarget = QBoardTarget.boardTarget;
        QFileDownloadHis qFileDownloadHis = QFileDownloadHis.fileDownloadHis;
        QCommonComment qCommonComment = QCommonComment.commonComment;
        QMyBoardData qMyBoardData = QMyBoardData.myBoardData;

        OrderSpecifier<Long> orderSpecifier = qBoardData.id.desc();
        OrderSpecifier<String> orderSpecifierString = qBoardData.ttl.asc();
        OrderSpecifier<LocalDateTime> orderSpecifierDateTime = qBoardData.ntceDt.desc();

        NumberPath<Long> asLikeCnt = Expressions.numberPath(Long.class, "likeCnt");

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qBoardData.mstPid.eq(form.getMstPid()));
        builder.and(qBoardData.delAt.eq("N"));
        builder.and(qBoardData.fixingAt.eq(form.getFixingAt()));

        if (searchForm.getLoginId() != null) {
            builder.and(qBoardData.regPsId.eq(searchForm.getLoginId()));
        }

        if (searchForm.getEduTarget() != null && !searchForm.getEduTarget().equals("")) {
            builder.and(qBoardTarget.mberDvTy.eq(MberDvType.valueOf(searchForm.getEduTarget())));
        }

        if (searchForm.getDataType() != null && !searchForm.getDataType().equals("")) {
            if (DataDvType.VIDEO.name().equals(searchForm.getDataType())) {
                builder.and(qFileInfo.flExtsn.in(FileUtilHelper.videoExt));
            } else if (DataDvType.DOCUMENT.name().equals(searchForm.getDataType())) {
                builder.and(qFileInfo.flExtsn.in(FileUtilHelper.docExt));
            } else if (DataDvType.IMAGE.name().equals(searchForm.getDataType())) {
                builder.and(qFileInfo.flExtsn.in(FileUtilHelper.imageExt));
            }
        }

        if (searchForm.getSorting() != null && !searchForm.getSorting().isEmpty()) {
            if (searchForm.getSorting().equals("like")) {
                orderSpecifier = asLikeCnt.desc();
            } else if (searchForm.getSorting().equals("readCnt")) {
                orderSpecifier = qBoardData.readCnt.longValue().desc();
            }
        }

        if (searchForm.getSrchField() != null && !searchForm.getSrchField().isEmpty()) {
            if (searchForm.getSrchField().equals("title")) {
                builder.and(qBoardData.ttl.like("%" + searchForm.getSrchWord() + "%"));
            } else if (searchForm.getSrchField().equals("cn")) {
                builder.and(qBoardData.cn.like("%" + searchForm.getSrchWord() + "%"));
            } else if (searchForm.getSrchField().equals("titleCn")) {
                builder.and(qBoardData.ttl.like("%" + searchForm.getSrchWord() + "%").or(qBoardData.cn.like("%" + searchForm.getSrchWord() + "%")));
            } else if (searchForm.getSrchField().equals("wrterNm")) {
                builder.and(qBoardData.wrterNm.like("%" + searchForm.getSrchWord() + "%"));
            }
        } else {
            if (searchForm.getSrchWord() != null && !searchForm.getSrchWord().isEmpty()) {
                builder.and(qBoardData.ttl.like("%" + searchForm.getSrchWord() + "%").or(qBoardData.cn.like("%" + searchForm.getSrchWord() + "%")));
            }
        }

        if (searchForm.getMyBoard() != null && !searchForm.getMyBoard().isEmpty()) {
            builder.and(qMyBoardData.mberPid.eq(form.getMberPid()));
        }

        if (searchForm.getSntncHead() != null && !searchForm.getSntncHead().isEmpty()) {
            builder.and(qBoardData.sntncHead.eq(searchForm.getSntncHead()));
        }

        JPAQuery<BoardData> tempList = queryFactory
                .select(Projections.fields(BoardData.class,
                        qBoardData.id,
                        qBoardData.thread,
                        qBoardData.depth,
                        qBoardData.sntncHead,
                        qBoardData.ttl,
                        qBoardData.cn,
                        qBoardData.wrterNm,
                        qBoardData.pwd,
                        qBoardData.readCnt,
                        qBoardData.wrterIp,
                        qBoardData.prntPwd,
                        qBoardData.regPsId,
                        qBoardData.regDtm,
                        qBoardData.updPsId,
                        qBoardData.updDtm,
                        qBoardData.ntceDt,
                        qBoardData.delAt,
                        qBoardData.fixingAt,
                        qBoardData.mstPid,
                        //qBoardTarget.mberDvTy.as("target"),
                        ExpressionUtils.as(
                                JPAExpressions.select(Expressions.stringTemplate("group_concat({0})", qBoardTarget.mberDvTy))
                                        .from(qBoardTarget)
                                        .where(qBoardTarget.dataPid.eq(qBoardData.id)),
                                "targetList"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qBoardData.regPsId)
                                                .and(qAccount.delAt.eq("N"))),
                                "regPsNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qFileInfo.flNm)
                                        .from(qFileInfo)
                                        .where(qFileInfo.dataPid.eq(qBoardData.id)
                                                .and(qFileInfo.dvTy.eq(FileDvType.THUMB.name())))
                                        .orderBy(qFileInfo.id.desc())
                                        .offset(1L),
                                "thumbFileNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qFileInfo.id.count())
                                        .from(qFileInfo)
                                        .innerJoin(qFileDownloadHis).on(qFileInfo.id.eq(qFileDownloadHis.flPid))
                                        .where(qFileInfo.dataPid.eq(qBoardData.id)
                                                .and(qFileInfo.tableNm.eq(TableNmType.TBL_BOARD_DATA.name()))),
                                "downloadCnt"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCommonComment.updDtm)
                                        .from(qCommonComment)
                                        .where(qCommonComment.dataPid.eq(qBoardData.id)
                                                .and(qCommonComment.tableNm.eq(TableNmType.TBL_BOARD_DATA))
                                                .and(qCommonComment.delAt.eq("N")))
                                        .groupBy(qCommonComment.dataPid, qCommonComment.tableNm, qCommonComment.delAt),
                                "commentDtm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCommonComment.id.count())
                                        .from(qCommonComment)
                                        .where(qCommonComment.dataPid.eq(qBoardData.id)
                                                .and(qCommonComment.tableNm.eq(TableNmType.TBL_BOARD_DATA))
                                                .and(qCommonComment.delAt.eq("N"))),
                                asLikeCnt)
                ))
                .from(qBoardData);
        if (searchForm.getEduTarget() != null && !searchForm.getEduTarget().equals("")) {
            tempList.leftJoin(qBoardTarget).on(qBoardData.id.eq(qBoardTarget.dataPid));
        }
        if (searchForm.getDataType() != null && !searchForm.getDataType().equals("")) {
            tempList.leftJoin(qFileInfo).on(qBoardData.id.eq(qFileInfo.dataPid).and(qFileInfo.dvTy.eq(FileDvType.ATTACH.name())));
        }
        //내교육자료 leftJoin
        if (searchForm.getMyBoard() != null && !searchForm.getMyBoard().equals("")) {
            tempList.leftJoin(qMyBoardData).on(qMyBoardData.dataPid.eq(qBoardData.id));
        }
        tempList.where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());
        if (searchForm.getSorting() != null && !searchForm.getSorting().isEmpty()) {
            if (searchForm.getSorting().equals("alphabetically")) {
                tempList.orderBy(orderSpecifierString);
            }
            if (searchForm.getSorting().equals("latest")) {
                tempList.orderBy(orderSpecifierDateTime);
            }
            if (searchForm.getSorting().equals("old")) {
                orderSpecifierDateTime = qBoardData.ntceDt.asc();
                tempList.orderBy(orderSpecifierDateTime);
            }
        }
        tempList.orderBy(orderSpecifier);

        QueryResults<BoardData> mngList = tempList.fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public List<BoardData> listForFix(BoardDataForm form) {

        QBoardData qBoardData = QBoardData.boardData;
        QCommonCode qCommonCode = QCommonCode.commonCode;
        QAccount qAccount = QAccount.account;
        QFileInfo qFileInfo = QFileInfo.fileInfo;
        QBoardTarget qBoardTarget = QBoardTarget.boardTarget;
        QFileDownloadHis qFileDownloadHis = QFileDownloadHis.fileDownloadHis;
        QCommonComment qCommonComment = QCommonComment.commonComment;
        QMyBoardData qMyBoardData = QMyBoardData.myBoardData;

        OrderSpecifier<Long> orderSpecifier = qBoardData.id.desc();

        NumberPath<Long> asLikeCnt = Expressions.numberPath(Long.class, "likeCnt");

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qBoardData.mstPid.eq(form.getMstPid()));
        builder.and(qBoardData.delAt.eq("N"));
        builder.and(qBoardData.fixingAt.eq("Y"));

        List<BoardData> mngList = queryFactory
                .select(Projections.fields(BoardData.class,
                        qBoardData.id,
                        qBoardData.thread,
                        qBoardData.depth,
                        qBoardData.sntncHead,
                        qBoardData.ttl,
                        qBoardData.cn,
                        qBoardData.wrterNm,
                        qBoardData.pwd,
                        qBoardData.readCnt,
                        qBoardData.wrterIp,
                        qBoardData.prntPwd,
                        qBoardData.regPsId,
                        qBoardData.regDtm,
                        qBoardData.updPsId,
                        qBoardData.updDtm,
                        qBoardData.ntceDt,
                        qBoardData.delAt,
                        qBoardData.fixingAt,
                        qBoardData.mstPid,
                        ExpressionUtils.as(
                                JPAExpressions.select(Expressions.stringTemplate("group_concat({0})", qBoardTarget.mberDvTy))
                                        .from(qBoardTarget)
                                        .where(qBoardTarget.dataPid.eq(qBoardData.id)),
                                "targetList"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qBoardData.regPsId)
                                                .and(qAccount.delAt.eq("N"))),
                                "regPsNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qFileInfo.flNm)
                                        .from(qFileInfo)
                                        .where(qFileInfo.dataPid.eq(qBoardData.id)
                                                .and(qFileInfo.dvTy.eq(FileDvType.THUMB.name())))
                                        .orderBy(qFileInfo.id.desc())
                                        .offset(1L),
                                "thumbFileNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qFileInfo.id.count())
                                        .from(qFileInfo)
                                        .innerJoin(qFileDownloadHis).on(qFileInfo.id.eq(qFileDownloadHis.flPid))
                                        .where(qFileInfo.dataPid.eq(qBoardData.id)
                                                .and(qFileInfo.tableNm.eq(TableNmType.TBL_BOARD_DATA.name()))),
                                "downloadCnt"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCommonComment.updDtm)
                                        .from(qCommonComment)
                                        .where(qCommonComment.dataPid.eq(qBoardData.id)
                                                .and(qCommonComment.tableNm.eq(TableNmType.TBL_BOARD_DATA))
                                                .and(qCommonComment.delAt.eq("N")))
                                        .groupBy(qCommonComment.dataPid, qCommonComment.tableNm, qCommonComment.delAt),
                                "commentDtm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCommonComment.id.count())
                                        .from(qCommonComment)
                                        .where(qCommonComment.dataPid.eq(qBoardData.id)
                                                .and(qCommonComment.tableNm.eq(TableNmType.TBL_BOARD_DATA))
                                                .and(qCommonComment.delAt.eq("N"))),
                                asLikeCnt)
                ))
                .from(qBoardData)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return mngList;
    }

    public BoardData load(BoardDataForm boardDataForm) {
        QBoardData qBoardData = QBoardData.boardData;
        QAccount qAccount = QAccount.account;
        QCommonComment qCommonComment = QCommonComment.commonComment;
        QBoardTarget qBoardTarget = QBoardTarget.boardTarget;
        QHashTag qHashTag = QHashTag.hashTag;

        StringPath hashTag = Expressions.stringPath("'#'");

        OrderSpecifier<Long> orderSpecifier = qBoardData.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        if (boardDataForm.getPrevNext() != null && !boardDataForm.getPrevNext().equals("")) {
            if (boardDataForm.getPrevNext().equals("prev")) {
                builder.and(qBoardData.id.lt(boardDataForm.getId())
                        .and(qBoardData.mstPid.eq(boardDataForm.getMstPid())));
                builder.and(qBoardData.fixingAt.eq("N"));
            }
            if (boardDataForm.getPrevNext().equals("next")) {
                orderSpecifier = qBoardData.id.asc();
                builder.and(qBoardData.id.gt(boardDataForm.getId())
                        .and(qBoardData.mstPid.eq(boardDataForm.getMstPid())));
                builder.and(qBoardData.fixingAt.eq("N"));
            }
        } else {
            builder.and(qBoardData.id.eq(boardDataForm.getId()));
        }
        /*if (boardDataForm.getMstPid() != policyProposalCdPid) {
            builder.and(qBoardData.delAt.eq("N"));
        }*/
        BoardData boardData = queryFactory
                .select(Projections.fields(BoardData.class,
                        qBoardData.id,
                        qBoardData.thread,
                        qBoardData.depth,
                        qBoardData.ttl,
                        qBoardData.cn,
                        qBoardData.wrterNm,
                        qBoardData.pwd,
                        qBoardData.readCnt,
                        qBoardData.wrterIp,
                        qBoardData.prntPwd,
                        qBoardData.regPsId,
                        qBoardData.regDtm,
                        qBoardData.updPsId,
                        qBoardData.updDtm,
                        qBoardData.ntceDt,
                        qBoardData.delAt,
                        qBoardData.fixingAt,
                        qBoardData.mstPid,
                        qBoardData.sntncHead,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qBoardData.regPsId)
                                                .and(qAccount.delAt.eq("N"))),
                                "regPsNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCommonComment.id.count())
                                        .from(qCommonComment)
                                        .where(qCommonComment.dataPid.eq(qBoardData.id)
                                                .and(qCommonComment.tableNm.eq(TableNmType.TBL_BOARD_DATA))
                                                .and(qCommonComment.delAt.eq("N"))),
                                "likeCnt"),
                        ExpressionUtils.as(
                                JPAExpressions.select(Expressions.stringTemplate("group_concat({0})", qBoardTarget.mberDvTy))
                                        .from(qBoardTarget)
                                        .where(qBoardTarget.dataPid.eq(qBoardData.id)),
                                "targetList"),
                        ExpressionUtils.as(
                                JPAExpressions.select(Expressions.stringTemplate("group_concat({0})", hashTag.concat(qHashTag.tagNm)))
                                        .from(qHashTag)
                                        .where(qHashTag.dataPid.eq(qBoardData.id).and(qHashTag.tableNm.eq(TableNmType.TBL_BOARD_DATA.name()))),
                                "hashTags")
                ))
                .from(qBoardData)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetchFirst();

        if (boardData != null && boardData.getMstPid() != policyProposalCdPid && !boardData.getDelAt().equals("N")) {
            boardData = new BoardData();
        }
        return boardData;
    }

    @Transactional
    public boolean delete(BoardDataForm boardDataForm) {
        try {
            BoardData mng = boardRepository.findById(boardDataForm.getId()).orElseGet(BoardData::new);

            mng.setUpdDtm(LocalDateTime.now());
            mng.setUpdPsId(boardDataForm.getUpdPsId());
            mng.setDelAt(boardDataForm.getDelAt());

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    @Transactional
    public boolean insert(BoardDataForm form, String[] targetArr, MultipartFile thumbFile, MultipartFile[] attachedFile, String[] tags) {
        try {
            if (form.getPwd() != null && !form.getPwd().equals("")) {
                form.setPwd(passwordEncoder.encode(form.getPwd()));
            }
            if (form.getFixingAt() == null || form.getFixingAt().equals("")) {
                form.setFixingAt("N");
            }
            if (form.getNtceDtString() == null || form.getNtceDtString().equals("")) {
                form.setNtceDt(LocalDateTime.now());
            } else {
                LocalDate aa = LocalDate.parse(form.getNtceDtString());
                LocalDateTime bb = LocalDateTime.of(aa, LocalDateTime.now().toLocalTime());
                form.setNtceDt(bb);
            }
            BoardData boardData = modelMapper.map(form, BoardData.class);
            BoardData save = boardRepository.save(boardData);

            if (tags != null) {
                for (String tag : tags) {
                    if (!tag.equals("")) {
                        HashTag hashTag = new HashTag();
                        hashTag.setTagNm(tag);
                        hashTag.setRegDtm(save.getRegDtm());
                        hashTag.setDataPid(save.getId());
                        hashTag.setTableNm(TableNmType.TBL_BOARD_DATA.name());
                        hashTagRepository.save(hashTag);
                    }
                }
            }

            if (targetArr != null) {
                for (String target : targetArr) {
                    BoardTarget boardTarget = new BoardTarget();
                    boardTarget.setDataPid(save.getId());
                    boardTarget.setMberDvTy(MberDvType.valueOf(target));
                    boardTargetRepository.save(boardTarget);
                }
            }

            if (thumbFile != null) {
                FileInfo fileInfo = FileUtilHelper.writeUploadedFile(thumbFile, Constants.FOLDERNAME_BOARDDATA, FileUtilHelper.imageExt);
                fileInfo.setDataPid(save.getId());
                TableNmType tblBoardData = TableNmType.TBL_BOARD_DATA;
                fileInfo.setTableNm(tblBoardData.name());
                fileInfo.setDvTy(FileDvType.THUMB.name());

                fileInfoRepository.save(fileInfo);
            }

            if (attachedFile != null) {
                for (MultipartFile multipartFile : attachedFile) {
                    if (multipartFile.isEmpty() == false) {
                        TableNmType tblBoardData = TableNmType.TBL_BOARD_DATA;
                        FileInfo fileInfo = FileUtilHelper.writeUploadedFile(multipartFile, Constants.FOLDERNAME_BOARDDATA);
                        if (fileInfo != null) {
                            fileInfo.setDataPid(save.getId());
                            fileInfo.setTableNm(tblBoardData.name());
                            fileInfo.setDvTy(FileDvType.ATTACH.name());
                            fileInfoRepository.save(fileInfo);
                        }
                    }
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean update(BoardDataForm form, String[] targetArr, MultipartFile thumbFile, MultipartFile[] attachedFiles, String[] tags) {
        try {
            if (form.getPwd() != null && !form.getPwd().equals("")) {
                form.setPwd(passwordEncoder.encode(form.getPwd()));
            }
            if (form.getFixingAt() == null || form.getFixingAt().equals("")) {
                form.setFixingAt("N");
            }
            if (form.getNtceDtString() == null || form.getNtceDtString().equals("")) {
                form.setNtceDt(LocalDateTime.now());
            } else {
                LocalDate aa = LocalDate.parse(form.getNtceDtString());
                LocalDateTime bb = LocalDateTime.of(aa, LocalDateTime.now().toLocalTime());
                form.setNtceDt(bb);
            }
            BoardData boardData = boardRepository.findById(form.getId()).get();
            boardData.setTtl(form.getTtl());
            boardData.setCn(form.getCn());
            boardData.setSntncHead(form.getSntncHead());
            //boardData.setWrterNm(form.getWrterNm());
            boardData.setPwd(form.getPwd());
            //boardData.setReadCnt(form.getReadCnt());
            boardData.setWrterIp(form.getWrterIp());
            boardData.setNtceDt(form.getNtceDt());
            boardData.setFixingAt(form.getFixingAt());
            boardData.setUpdPsId(form.getUpdPsId());
            boardData.setUpdDtm(LocalDateTime.now());

            if (tags != null) {
                hashTagRepository.deleteByDataPidAndTableNm(boardData.getId(), TableNmType.TBL_BOARD_DATA.name());
                for (String tag : tags) {
                    if (!tag.equals("")) {
                        HashTag hashTag = new HashTag();
                        hashTag.setTagNm(tag);
                        hashTag.setRegDtm(boardData.getUpdDtm());
                        hashTag.setDataPid(boardData.getId());
                        hashTag.setTableNm(TableNmType.TBL_BOARD_DATA.name());
                        hashTagRepository.save(hashTag);
                    }
                }
            }

            boardTargetRepository.deleteAllByDataPid(boardData.getId());

            if (targetArr != null) {
                for (String target : targetArr) {
                    BoardTarget save = new BoardTarget();
                    save.setDataPid(boardData.getId());
                    save.setMberDvTy(MberDvType.valueOf(target));
                    boardTargetRepository.save(save);
                }
            }

            if (thumbFile != null && thumbFile.isEmpty() == false) {
                TableNmType tblBoardData = TableNmType.TBL_BOARD_DATA;
                FileInfo fileInfo = FileUtilHelper.writeUploadedFile(thumbFile, Constants.FOLDERNAME_BOARDDATA, FileUtilHelper.imageExt);
                if (fileInfo != null) {

                    fileInfo.setDataPid(boardData.getId());
                    fileInfo.setTableNm(tblBoardData.name());
                    fileInfo.setDvTy(FileDvType.THUMB.name());

                    fileInfoRepository.deleteByData(fileInfo.getDataPid(), fileInfo.getTableNm(), fileInfo.getDvTy());
                    fileInfoRepository.save(fileInfo);
                }
            }

            if (attachedFiles.length > 0) {
                for (MultipartFile multipartFile : attachedFiles) {
                    if (multipartFile.isEmpty() == false) {
                        TableNmType tblBoardData = TableNmType.TBL_BOARD_DATA;
                        FileInfo fileInfo = FileUtilHelper.writeUploadedFile(multipartFile, Constants.FOLDERNAME_BOARDDATA);
                        if (fileInfo != null) {
                            fileInfo.setDataPid(boardData.getId());
                            fileInfo.setTableNm(tblBoardData.name());
                            fileInfo.setDvTy(FileDvType.ATTACH.name());
                            //제안일경우첨부파일삭제
                            if (form.getMstPid().equals(policyProposalCdPid)) {
                                fileInfoRepository.deleteByData(fileInfo.getDataPid(), fileInfo.getTableNm(), fileInfo.getDvTy());
                            }
                            fileInfoRepository.save(fileInfo);
                        }
                    }
                }
            }
            /*if (attachedFile != null) {
                FileInfo fileInfo = FileUtilHelper.writeUploadedFile(attachedFile, Constants.FOLDERNAME_BOARDDATA);
                fileInfo.setDataPid(form.getId());
                TableNmType tblBoardData = TableNmType.TBL_BOARD_DATA;
                fileInfo.setTableNm(tblBoardData.name());

                fileInfoRepository.save(fileInfo);
            }*/

            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Transactional
    public boolean updateByReadCnt(BoardDataForm form) {
        try {
            BoardData boardData = boardRepository.findById(form.getId()).get();
            boardData.setReadCnt(form.getReadCnt());

            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public Long count(BoardDataForm boardDAtaForm) {
        QBoardData boardData = QBoardData.boardData;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(boardData.delAt.eq("N"));
        builder.and(boardData.regPsId.eq(boardDAtaForm.getRegPsId()));
        builder.and(boardData.mstPid.eq(boardDAtaForm.getMstPid()));

        Long count = queryFactory
                .select(Projections.fields(BoardData.class,
                        boardData.id
                ))
                .from(boardData)
                .where(builder)
                .fetchCount();

        return count;
    }

    public Page<SearchDto> totalSearchList(Pageable pageable, SearchForm searchForm) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, (searchForm.getPageSize() == null ? Constants.DEFAULT_PAGESIZE : searchForm.getPageSize())); // <- Sort 추가
        Page<Object[]> searchList = boardRepository.totalSearchList(pageable, searchForm);

        List<SearchDto> tempList = new ArrayList<>();

        if (searchList != null) {
            for (Object[] objects : searchList) {
                SearchDto searchDto = new SearchDto();
                searchDto.setDataId(((BigInteger) objects[0]).longValue());
                searchDto.setTtl((String) objects[1]);
                searchDto.setCn((String) objects[2]);
                searchDto.setRegDtm(objects[3] == null ? null : ((Timestamp) objects[3]).toLocalDateTime());
                searchDto.setMenuNm((String) objects[4]);
                searchDto.setUrl((String) objects[5]);
                searchDto.setTag((String) objects[6]);

                tempList.add(searchDto);
            }
        }

        Page<SearchDto> rtnList = new PageImpl<>(tempList, searchList.getPageable(), searchList.getTotalElements());

        return rtnList;
    }
}
