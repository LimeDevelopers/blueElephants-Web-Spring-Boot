package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.enums.FileDvType;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.repository.web.CampaignRepository;
import kr.or.btf.web.repository.web.FileInfoRepository;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.CampaignForm;
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
public class CampaignService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final CampaignRepository campaignRepository;
    private final ModelMapper modelMapper;
    private final FileInfoRepository fileInfoRepository;

    public Page<Campaign> list(Pageable pageable, SearchForm searchForm, CampaignForm campaignForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, (searchForm.getPageSize() == null ? Constants.DEFAULT_PAGESIZE : searchForm.getPageSize())); // <- Sort 추가

        QCampaign qCampaign = QCampaign.campaign;
        QCommonCode qCommonCode = QCommonCode.commonCode;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qCampaign.id.desc();
        OrderSpecifier<String> orderSpecifierString = qCampaign.ttl.asc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCampaign.delAt.eq("N"));

        if (campaignForm.getDvCodePid() != null) {
            builder.and(qCampaign.dvCodePid.eq(campaignForm.getDvCodePid()));
        }

        if (searchForm.getSorting() != null && !searchForm.getSorting().isEmpty()) {
            if (searchForm.getSorting().equals("old")) {
                orderSpecifier = qCampaign.id.asc();
            } else if (searchForm.getSorting().equals("readCnt")) {
                orderSpecifier = qCampaign.readCnt.longValue().desc();
            }
        }

        if (searchForm.getSrchWord() != null && !searchForm.getSrchWord().isEmpty()) {
            builder.and(qCampaign.ttl.like("%" + searchForm.getSrchWord() + "%"));
        }

        if (searchForm.getSrchField() != null && !searchForm.getSrchField().isEmpty()) {
            if (searchForm.getSrchField().equals("title")) {
                builder.and(qCampaign.ttl.like("%" + searchForm.getSrchWord() + "%"));
            } else if (searchForm.getSrchField().equals("cn")) {
                builder.and(qCampaign.cn.like("%" + searchForm.getSrchWord() + "%"));
            } else if (searchForm.getSrchField().equals("titleCn")) {
                builder.and(qCampaign.ttl.like("%" + searchForm.getSrchWord() + "%").or(qCampaign.cn.like("%" + searchForm.getSrchWord() + "%")));
            } else if (searchForm.getSrchField().equals("wrterNm")) {
                builder.and(qAccount.nm.like("%" + searchForm.getSrchWord() + "%"));
            }
        } else {
            if (searchForm.getSrchWord() != null && !searchForm.getSrchWord().isEmpty()) {
                builder.and(qCampaign.ttl.like("%" + searchForm.getSrchWord() + "%").or(qCampaign.cn.like("%" + searchForm.getSrchWord() + "%")));
            }
        }

        JPAQuery<Campaign> tempList = queryFactory
                .select(Projections.fields(Campaign.class,
                        qCampaign.id,
                        qCampaign.ttl,
                        qCampaign.cn,
                        qCampaign.actCn,
                        qCampaign.imgFl,
                        qCampaign.cntntsUrl,
                        qCampaign.dvCodePid,
                        qCampaign.regDtm,
                        qCampaign.regPsId,
                        qCampaign.readCnt,
                        qCampaign.delAt,
                        qAccount.nm.as("regPsNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCommonCode.codeNm)
                                        .from(qCommonCode)
                                        .where(qCommonCode.id.eq(qCampaign.dvCodePid)),
                                "dvCodeNm")
                ))
                .from(qCampaign)
                .leftJoin(qAccount).on(qAccount.loginId.eq(qCampaign.regPsId))
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());
        if (searchForm.getSorting() != null && !searchForm.getSorting().isEmpty()) {
            if (searchForm.getSorting().equals("alphabetically")) {
                tempList.orderBy(orderSpecifierString);
            }
        }
        tempList.orderBy(orderSpecifier);

        QueryResults<Campaign> mngList = tempList.fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Campaign load(Long id) {
        //Campaign campaign = campaignRepository.findById(id).orElseGet(Campaign::new);

        QCampaign qCampaign = QCampaign.campaign;
        QAccount qAccount = QAccount.account;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCampaign.id.eq(id));

        Campaign campaign = queryFactory
                .select(Projections.fields(Campaign.class,
                        qCampaign.id,
                        qCampaign.ttl,
                        qCampaign.cn,
                        qCampaign.actCn,
                        qCampaign.imgFl,
                        qCampaign.cntntsDvTy,
                        qCampaign.cntntsUrl,
                        qCampaign.dvCodePid,
                        qCampaign.readCnt,
                        qCampaign.regPsId,
                        qCampaign.regDtm,
                        qCampaign.updPsId,
                        qCampaign.updDtm,
                        qCampaign.delAt,
                        qAccount.nm.as("regPsNm")
                        ))
                .from(qCampaign)
                .leftJoin(qAccount).on(qAccount.loginId.eq(qCampaign.regPsId))
                .where(builder)
                .fetchOne();

        return campaign;
    }

    public Campaign latestOneLoad(CampaignForm form) {

        QCampaign qCampaign = QCampaign.campaign;
        QCommonCode qCommonCode = QCommonCode.commonCode;
        QAccount qAccount = QAccount.account;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCampaign.delAt.eq("N"));
        builder.and(qCampaign.dvCodePid.eq(form.getDvCodePid()));

        OrderSpecifier<Long> orderSpecifier = qCampaign.id.desc();


        Campaign mng = queryFactory
                .select(Projections.fields(Campaign.class,
                        qCampaign.id,
                        qCampaign.ttl,
                        qCampaign.cn,
                        qCampaign.actCn,
                        qCampaign.imgFl,
                        qCampaign.cntntsDvTy,
                        qCampaign.cntntsUrl,
                        qCampaign.dvCodePid,
                        qCampaign.regDtm,
                        qCampaign.regPsId,
                        ExpressionUtils.as(
                                JPAExpressions.select(qCommonCode.codeNm)
                                        .from(qCommonCode)
                                        .where(qCommonCode.id.eq(qCampaign.dvCodePid)),
                                "dvCodeNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qCampaign.regPsId)),
                                "regPsNm")
                ))
                .from(qCampaign)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetchFirst();

        if (mng != null) {
            return mng;
        } else {
            return new Campaign();
        }

    }

    @Transactional
    public void delete(CampaignForm campaignForm) {
        Campaign mng = campaignRepository.findById(campaignForm.getId()).orElseGet(Campaign::new);

        mng.setUpdDtm(LocalDateTime.now());
        mng.setUpdPsId(campaignForm.getUpdPsId());
        mng.setDelAt(campaignForm.getDelAt());
    }

    /**
     * @param campaignForm
     * @return
     */
    @Transactional
    public Campaign insert(CampaignForm campaignForm, MultipartFile attachImgFl, MultipartFile[] attachedFile) throws Exception {

        if (attachImgFl != null) {
            FileInfo fileInfo = FileUtilHelper.writeUploadedFile(attachImgFl, Constants.FOLDERNAME_CAMPAIGN, FileUtilHelper.imageExt);
            campaignForm.setImgFl(fileInfo.getFlNm());
        }

        Campaign campaign = modelMapper.map(campaignForm, Campaign.class);
        campaign = campaignRepository.save(campaign);

        if (attachedFile != null) {
            for (MultipartFile multipartFile : attachedFile) {
                if(multipartFile.isEmpty() == false) {
                    TableNmType tblCommonComment = TableNmType.TBL_CAMPAIGN;
                    FileInfo fileInfo = FileUtilHelper.writeUploadedFile(multipartFile, Constants.FOLDERNAME_CAMPAIGN);
                    if (fileInfo != null) {
                        fileInfo.setDataPid(campaign.getId());
                        fileInfo.setTableNm(tblCommonComment.name());
                        fileInfo.setDvTy(FileDvType.ATTACH.name());
                        fileInfoRepository.save(fileInfo);
                    }
                }
            }
        }

        return campaign;
    }

    @Transactional
    public boolean update(CampaignForm campaignForm, MultipartFile attachImgFl, MultipartFile[] attachedFile) {

        try {
            FileInfo fileInfo = new FileInfo();
            if (attachImgFl.isEmpty() == false) {
                fileInfo = FileUtilHelper.writeUploadedFile(attachImgFl, Constants.FOLDERNAME_CAMPAIGN, FileUtilHelper.imageExt);
            }

            Campaign campaign = campaignRepository.findById(campaignForm.getId()).orElseGet(Campaign::new);
            campaign.setTtl(campaignForm.getTtl());
            campaign.setCn(campaignForm.getCn());
            campaign.setActCn(campaignForm.getActCn());
            if (fileInfo.getFlNm() != null) {
                campaign.setImgFl(fileInfo.getFlNm());
            }
            campaign.setCntntsDvTy(campaignForm.getCntntsDvTy());
            campaign.setCntntsUrl(campaignForm.getCntntsUrl());

            campaign.setUpdPsId(campaignForm.getUpdPsId());
            campaign.setUpdDtm(LocalDateTime.now());

            if(attachedFile != null) {
                if (attachedFile.length > 0) {
                    for (MultipartFile multipartFile : attachedFile) {
                        if(multipartFile.isEmpty() == false) {
                            TableNmType tblCommonComment = TableNmType.TBL_CAMPAIGN;
                            fileInfo = FileUtilHelper.writeUploadedFile(multipartFile, Constants.FOLDERNAME_CAMPAIGN);
                            if (fileInfo != null) {
                                fileInfo.setDataPid(campaign.getId());
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

    @Transactional
    public boolean updateByReadCnt(CampaignForm form) {
        try{
            Campaign campaign = campaignRepository.findById(form.getId()).get();
            campaign.setReadCnt(form.getReadCnt());

            return true;
        }catch(Exception e){
            return false;
        }

    }
}
