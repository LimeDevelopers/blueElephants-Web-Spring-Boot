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
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QCommonCode;
import kr.or.btf.web.domain.web.QPostscript;
import kr.or.btf.web.domain.web.enums.FileDvType;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.repository.web.FileInfoRepository;
import kr.or.btf.web.repository.web.PostscriptImageRepository;
import kr.or.btf.web.repository.web.PostscriptRepository;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.PostscriptForm;
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
public class PostscriptService {
    private final JPAQueryFactory queryFactory;
    private final PostscriptRepository postscriptRepository;
    private final ModelMapper modelMapper;
    private final FileInfoRepository fileInfoRepository;
    private final PostscriptImageRepository postscriptImageRepository;


    public Page<Postscript> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, (searchForm.getPageSize() == null ? Constants.DEFAULT_PAGESIZE : searchForm.getPageSize())); // <- Sort 추가

        QPostscript qPostscript = QPostscript.postscript;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCodeP = new QCommonCode("qCommonCodeP");
        QCommonCode qCommonCodeC = new QCommonCode("qCommonCodeC");


        OrderSpecifier<Long> orderSpecifier = qPostscript.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qPostscript.delAt.eq("N"));

        if (searchForm.getSrchField() != null && !searchForm.getSrchField().isEmpty()) {
            if (searchForm.getSrchField().equals("title")) {
                builder.and(qPostscript.ttl.like("%" + searchForm.getSrchWord() + "%"));
            } else if (searchForm.getSrchField().equals("cn")) {
                builder.and(qPostscript.cn.like("%" + searchForm.getSrchWord() + "%"));
            } else if (searchForm.getSrchField().equals("wrterNm")) {
                builder.and(qAccount.nm.like("%" + searchForm.getSrchWord() + "%"));
            }
        } else {
            if (searchForm.getSrchWord() != null && !searchForm.getSrchWord().isEmpty()) {
                builder.and(qPostscript.ttl.like("%" + searchForm.getSrchWord() + "%").or(qPostscript.cn.like("%" + searchForm.getSrchWord() + "%")));
                //builder.and(qPostscript.ttl.like("%" + searchForm.getSrchWord() + "%").or(qPostscript.cn.like("%" + searchForm.getSrchWord() + "%").or(qAccount.nm.like("%" + searchForm.getSrchWord() + "%"))));
            }
        }

        if (searchForm.getSrchGbnParent() != null) {
            builder.and(qCommonCodeP.id.eq(searchForm.getSrchGbnParent()));
        }
        if (searchForm.getSrchGbnChild() != null) {
            builder.and(qCommonCodeC.id.eq(searchForm.getSrchGbnChild()));
        }

        QueryResults<Postscript> mngList = queryFactory
                .select(Projections.fields(Postscript.class,
                        qPostscript.id,
                        qPostscript.areaNm,
                        qPostscript.schlNm,
                        qPostscript.grade,
                        qPostscript.ban,
                        qPostscript.no,
                        qPostscript.srtCodePid,
                        qPostscript.ttl,
                        qPostscript.cn,
                        qPostscript.cntntsUrl,
                        qPostscript.imgFl,
                        qPostscript.regPsId,
                        qPostscript.regDtm,
                        qPostscript.updPsId,
                        qPostscript.updDtm,
                        qPostscript.delAt,
                        qCommonCodeP.codeNm.as("srtParentCodeNm"),
                        qCommonCodeC.codeNm.as("srtCodeNm"),
                        qAccount.nm.as("regPsNm")/*,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qPostscript.regPsId)),
                                "regPsNm")*/

                ))
                .from(qPostscript)
                .innerJoin(qCommonCodeC).on(qPostscript.srtCodePid.eq(qCommonCodeC.id))
                .innerJoin(qCommonCodeP).on(qCommonCodeC.prntCodePid.eq(qCommonCodeP.id))
                .innerJoin(qAccount).on(qAccount.loginId.eq(qPostscript.regPsId))
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Postscript load(Long id) {
        Postscript request = postscriptRepository.findById(id).orElseGet(Postscript::new);

        return request;
    }

    public Postscript loadByForm(PostscriptForm form) {

        QPostscript qPostscript = QPostscript.postscript;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCodeParent = new QCommonCode("parentCommonCode");
        QCommonCode qCommonCode = new QCommonCode("commonCode");

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qPostscript.delAt.eq("N"));

        if (form.getAreaNm() != null) {
            builder.and(qPostscript.areaNm.eq(form.getAreaNm()));
        }
        if (form.getSchlNm() != null) {
            builder.and(qPostscript.schlNm.eq(form.getSchlNm()));
        }
        if (form.getGrade() != null) {
            builder.and(qPostscript.grade.eq(form.getGrade()));
        }
        if (form.getId() != null) {
            builder.and(qPostscript.id.eq(form.getId()));
        }
        if (form.getSrtCodePid() != null) {
            builder.and(qPostscript.srtCodePid.eq(form.getSrtCodePid()));
        }

        Postscript object = queryFactory
                .select(Projections.fields(Postscript.class,
                        qPostscript.id,
                        qPostscript.areaNm,
                        qPostscript.schlNm,
                        qPostscript.grade,
                        qPostscript.ban,
                        qPostscript.no,
                        qPostscript.srtCodePid,
                        qPostscript.ttl,
                        qPostscript.cn,
                        qPostscript.cntntsUrl,
                        qPostscript.imgFl,
                        qPostscript.regPsId,
                        qPostscript.regDtm,
                        qPostscript.updPsId,
                        qPostscript.updDtm,
                        qPostscript.delAt,
                        qCommonCode.prntCodePid.as("srtParentCodePid"),
                        qCommonCodeParent.codeNm.as("srtParentCodeNm"),
                        qCommonCode.codeNm.as("srtCodeNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qPostscript.regPsId)),
                                "regPsNm")
                ))
                .from(qPostscript)
                .innerJoin(qCommonCode).on(qPostscript.srtCodePid.eq(qCommonCode.id))
                .leftJoin(qCommonCodeParent).on(qCommonCode.prntCodePid.eq(qCommonCodeParent.id))
                .where(builder)
                .fetchOne();

        return object;
    }

    /**
     * @param postscriptForm
     * @return
     */

    @Transactional
    public boolean insert(PostscriptForm postscriptForm, MultipartFile imgFl, MultipartFile[] attachedFiles, String[] dsc) throws Exception {

        Postscript postscript = modelMapper.map(postscriptForm, Postscript.class);

        //썸네일
        TableNmType tblBoardData = TableNmType.TBL_POSTSCRIPT;
        FileInfo thumb = FileUtilHelper.writeUploadedFile(imgFl, Constants.FOLDERNAME_POSTSCRIPT, FileUtilHelper.imageExt);
        if (thumb != null) {
            thumb.setDataPid(postscript.getId());
            thumb.setTableNm(tblBoardData.name());
            thumb.setDvTy(FileDvType.THUMB.name());
            postscript.setImgFl(thumb.getChgFlNm());
        }
        postscript = postscriptRepository.save(postscript);

        //첨부파일
        if (attachedFiles != null && attachedFiles.length > 0) {
            int idx = 0;
            for (MultipartFile multipartFile : attachedFiles) {
                if (multipartFile.isEmpty() == false) {
                    FileInfo fileInfo = FileUtilHelper.writeUploadedFile(multipartFile, Constants.FOLDERNAME_POSTSCRIPT, FileUtilHelper.imageExt);
                    if (fileInfo != null) {
                        fileInfo.setDataPid(postscript.getId());
                        fileInfo.setTableNm(tblBoardData.name());
                        fileInfo.setDvTy(FileDvType.ATTACH.name());
                        FileInfo save = fileInfoRepository.save(fileInfo);

                        PostscriptImage postscriptImage = new PostscriptImage();
                        postscriptImage.setFlPid(save.getId());
                        postscriptImage.setPostscriptPid(postscript.getId());
                        postscriptImage.setDsc(dsc[idx]);
                        postscriptImage.setSn(idx);
                        postscriptImageRepository.save(postscriptImage);
                        idx++;
                    }
                }
            }
        }

        return true;
    }

    @Transactional
    public boolean update(PostscriptForm postscriptForm, MultipartFile imgFl, String[] dsc) {

        try {
            Postscript postscript = postscriptRepository.findById(postscriptForm.getId()).orElseGet(Postscript::new);
            postscript.setId(postscriptForm.getId());

            postscript.setTtl(postscriptForm.getTtl());
            postscript.setCn(postscriptForm.getCn());
            postscript.setCntntsUrl(postscriptForm.getCntntsUrl());
            postscript.setSrtCodeNm(postscript.getSrtCodeNm());
            postscript.setUpdPsId(postscriptForm.getUpdPsId());
            postscript.setUpdDtm(LocalDateTime.now());
            postscript.setSrtCodePid(postscriptForm.getSrtCodePid());

            //썸네일
            TableNmType tblBoardData = TableNmType.TBL_POSTSCRIPT;
            FileInfo thumb = FileUtilHelper.writeUploadedFile(imgFl, Constants.FOLDERNAME_POSTSCRIPT, FileUtilHelper.imageExt);
            if (thumb != null) {
                thumb.setDataPid(postscript.getId());
                thumb.setTableNm(tblBoardData.name());
                thumb.setDvTy(FileDvType.THUMB.name());
                postscript.setImgFl(thumb.getChgFlNm());
            }
            //postscript = postscriptRepository.save(postscript);

            List<PostscriptImage> allByPostscriptPid = postscriptImageRepository.findAllByPostscriptPid(postscript.getId());
            int idx = 0;
            for (PostscriptImage item : allByPostscriptPid) {
                PostscriptImage postscriptImage = postscriptImageRepository.findById(item.getId()).orElseGet(PostscriptImage::new);
                postscriptImage.setDsc(dsc[idx++]);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean updateForRegstUser(PostscriptForm postscriptForm, Long[] imagePid, MultipartFile thumbnailFile, MultipartFile[] attachedFiles, String[] dsc) {
        try {
            Postscript postscript = postscriptRepository.findById(postscriptForm.getId()).orElseGet(Postscript::new);
            postscript.setId(postscriptForm.getId());

            postscript.setTtl(postscriptForm.getTtl());
            postscript.setCn(postscriptForm.getCn());
            postscript.setCntntsUrl(postscriptForm.getCntntsUrl());
            postscript.setSrtCodeNm(postscript.getSrtCodeNm());
            postscript.setUpdPsId(postscriptForm.getUpdPsId());
            postscript.setUpdDtm(LocalDateTime.now());
            postscript.setSrtCodePid(postscriptForm.getSrtCodePid());

            //썸네일
            TableNmType tblBoardData = TableNmType.TBL_POSTSCRIPT;
            FileInfo thumb = FileUtilHelper.writeUploadedFile(thumbnailFile, Constants.FOLDERNAME_POSTSCRIPT, FileUtilHelper.imageExt);
            if (thumb != null) {
                //thumb.setDataPid(postscript.getId());
                //thumb.setTableNm(tblBoardData.name());
                //thumb.setDvTy(FileDvType.THUMB.name());
                postscript.setImgFl(thumb.getChgFlNm());
            }
            //postscript = postscriptRepository.save(postscript);

            //List<PostscriptImage> allByPostscriptPid = postscriptImageRepository.findAllByPostscriptPid(postscript.getId());
            //int idx =

              int idx = 0;
            for (Long aLong : imagePid) {
                if (aLong != 0l) {
                    PostscriptImage image = postscriptImageRepository.findById(aLong).orElseGet(PostscriptImage::new);
                    image.setDsc(dsc[idx]);
                    image.setSn(idx);
                } else {
                    if (attachedFiles != null && attachedFiles.length > idx && attachedFiles[idx].isEmpty() == false) {
                        FileInfo fileInfo = FileUtilHelper.writeUploadedFile(attachedFiles[idx], Constants.FOLDERNAME_POSTSCRIPT, FileUtilHelper.imageExt);
                        if (fileInfo != null) {
                            fileInfo.setDataPid(postscript.getId());
                            fileInfo.setTableNm(tblBoardData.name());
                            fileInfo.setDvTy(FileDvType.ATTACH.name());
                            FileInfo save = fileInfoRepository.save(fileInfo);

                            PostscriptImage postscriptImage = new PostscriptImage();
                            postscriptImage.setFlPid(save.getId());
                            postscriptImage.setPostscriptPid(postscript.getId());
                            postscriptImage.setDsc(dsc[idx]);
                            postscriptImage.setSn((idx));
                            postscriptImageRepository.save(postscriptImage);
                        }
                    }
                }
                idx++;
            }

            //첨부파일
            /*if (attachedFiles != null && attachedFiles.length > 0) {
                int idx = 0;
                for (MultipartFile multipartFile : attachedFiles) {
                    if (multipartFile.isEmpty() == false) {
                        FileInfo fileInfo = FileUtilHelper.writeUploadedFile(multipartFile, Constants.FOLDERNAME_POSTSCRIPT, FileUtilHelper.imageExt);
                        if (fileInfo != null) {
                            fileInfo.setDataPid(postscript.getId());
                            fileInfo.setTableNm(tblBoardData.name());
                            fileInfo.setDvTy(FileDvType.ATTACH.name());
                            FileInfo save = fileInfoRepository.save(fileInfo);

                            PostscriptImage postscriptImage = new PostscriptImage();
                            postscriptImage.setFlPid(save.getId());
                            postscriptImage.setPostscriptPid(postscript.getId());
                            postscriptImage.setDsc(dsc[idx]);
                            postscriptImage.setSn(idx);
                            postscriptImageRepository.save(postscriptImage);
                            idx++;
                        }
                    }
                }
            }*/

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public void delete(PostscriptForm postscriptForm) {
        Postscript mng = this.load(postscriptForm.getId());

        mng.setUpdDtm(LocalDateTime.now());
        mng.setUpdPsId(postscriptForm.getUpdPsId());
        mng.setDelAt(postscriptForm.getDelAt());
    }


}
