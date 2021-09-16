package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.enums.FileDvType;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.repository.web.ExperienceRepository;
import kr.or.btf.web.repository.web.ExperienceTargetRepository;
import kr.or.btf.web.repository.web.FileInfoRepository;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.ExperienceForm;
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
public class ExperienceService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final ExperienceRepository experienceRepository;
    private final ExperienceTargetRepository experienceTargetRepository;
    private final ModelMapper modelMapper;
    private final FileInfoRepository fileInfoRepository;

    public Page<Experience> list(Pageable pageable, SearchForm searchForm, ExperienceForm experienceForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, (searchForm.getPageSize() == null ? Constants.DEFAULT_PAGESIZE : searchForm.getPageSize())); // <- Sort 추가

        QExperience qExperience = QExperience.experience;
        QAccount qAccount = QAccount.account;
        QFileInfo qFileInfo = QFileInfo.fileInfo;

        OrderSpecifier<Long> orderSpecifier = qExperience.id.desc();
        OrderSpecifier<String> orderSpecifierString = qExperience.ttl.asc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qExperience.delAt.eq("N"));

        if (experienceForm.getDvCodePid() != null) {
            builder.and(qExperience.dvCodePid.eq(experienceForm.getDvCodePid()));
        }

        if (searchForm.getSorting() != null && !searchForm.getSorting().isEmpty()) {
            if (searchForm.getSorting().equals("old")) {
                orderSpecifier = qExperience.id.asc();
            } else if (searchForm.getSorting().equals("readCnt")) {
                orderSpecifier = qExperience.readCnt.longValue().desc();
            }
        }

        if (searchForm.getSrchFile() != null && !searchForm.getSrchFile().isEmpty()) {
            if (searchForm.getSrchFile().equals("1")) {
                builder.and(qExperience.imgFl.isNotNull().and(qExperience.cntntsUrl.ne(""))
                .and(qFileInfo.flNm.isNull()));
            }

            if (searchForm.getSrchFile().equals("2")) {
                builder.and(qExperience.imgFl.isNotNull().and(qFileInfo.flNm.isNotNull())
                        .and(qExperience.cntntsUrl.eq("")));
            }

            if (searchForm.getSrchFile().equals("3")) {
                builder.and(qExperience.imgFl.isNotNull().and(qExperience.cn.eq(""))
                        .and(qFileInfo.flNm.isNull()).and(qExperience.cntntsUrl.eq("")));
            }

            if (searchForm.getSrchFile().equals("4")) {
                builder.and(qExperience.imgFl.isNotNull().and(qExperience.cn.ne(""))
                        .and(qFileInfo.flNm.isNull()).and(qExperience.cntntsUrl.eq("")));
            }
        }

        /*if (searchForm.getSrchWord() != null && !searchForm.getSrchWord().isEmpty()) {
            builder.and(qExperience.ttl.like("%" + searchForm.getSrchWord() + "%"));
        }*/

        if (searchForm.getSrchField() != null && !searchForm.getSrchField().isEmpty()) {
            if (searchForm.getSrchField().equals("title")) {
                builder.and(qExperience.ttl.like("%" + searchForm.getSrchWord() + "%"));
            } else if (searchForm.getSrchField().equals("cn")) {
                builder.and(qExperience.cn.like("%" + searchForm.getSrchWord() + "%"));
            } else if (searchForm.getSrchField().equals("titleCn")) {
                builder.and(qExperience.ttl.like("%" + searchForm.getSrchWord() + "%").or(qExperience.cn.like("%" + searchForm.getSrchWord() + "%")));
            } else if (searchForm.getSrchField().equals("wrterNm")) {
                builder.and(qAccount.nm.like("%" + searchForm.getSrchWord() + "%"));
            }
        } else {
            if (searchForm.getSrchWord() != null && !searchForm.getSrchWord().isEmpty()) {
                builder.and(qExperience.ttl.like("%" + searchForm.getSrchWord() + "%").or(qExperience.cn.like("%" + searchForm.getSrchWord() + "%")));
            }
        }

        JPAQuery<Experience> tempList = queryFactory
                .select(Projections.fields(Experience.class,
                        qExperience.id,
                        qExperience.ttl,
                        qExperience.cn,
                        qExperience.imgFl,
                        qExperience.readCnt,
                        qExperience.cntntsDvTy,
                        qExperience.cntntsUrl,
                        qExperience.dvCodePid,
                        qFileInfo.chgFlNm,
                        qFileInfo.flNm,
                        qExperience.regDtm,
                        qAccount.nm.as("regPsNm")
                ))
                .from(qExperience)
                .leftJoin(qFileInfo).on(qFileInfo.dataPid.eq(qExperience.id).and(qFileInfo.tableNm.eq(TableNmType.TBL_EXPERIENCE.name()))
                        .and(qFileInfo.dvTy.eq(FileDvType.ATTACH.name())))
                .leftJoin(qAccount).on(qAccount.loginId.eq(qExperience.regPsId))
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());
        if (searchForm.getSorting() != null && !searchForm.getSorting().isEmpty()) {
            if (searchForm.getSorting().equals("alphabetically")) {
                tempList.orderBy(orderSpecifierString);
            }
        }
        tempList.orderBy(orderSpecifier);

        QueryResults<Experience> mngList = tempList.fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Experience load(Long id) {
        Experience experience = experienceRepository.findById(id).orElseGet(Experience::new);

        return experience;
    }

    public Experience loadByform(ExperienceForm form) {

        QExperience qExperience = QExperience.experience;
        QAccount qAccount = QAccount.account;
        QFileInfo qFileInfo = QFileInfo.fileInfo;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qExperience.delAt.eq("N"));
        builder.and(qExperience.id.eq(form.getId()));

        List<Experience> list = queryFactory
                .select(Projections.fields(Experience.class,
                        qExperience.id,
                        qExperience.ttl,
                        qExperience.cn,
                        qExperience.imgFl,
                        qExperience.readCnt,
                        qExperience.cntntsDvTy,
                        qExperience.cntntsUrl,
                        qExperience.dvCodePid,
                        qExperience.regDtm,
                        qFileInfo.chgFlNm,
                        qFileInfo.flNm
                ))
                .from(qExperience)
                .leftJoin(qFileInfo).on(qFileInfo.dataPid.eq(qExperience.id).and(qFileInfo.tableNm.eq(TableNmType.TBL_EXPERIENCE.name()))
                        .and(qFileInfo.dvTy.eq(FileDvType.ATTACH.name())))
                .where(builder)
                .fetch();

        return (list != null && list.size() > 0 ? list.get(0) : new Experience());
    }

    @Transactional
    public void delete(ExperienceForm experienceForm) {
        Experience mng = this.load(experienceForm.getId());

        mng.setUpdDtm(LocalDateTime.now());
        mng.setUpdPsId(experienceForm.getUpdPsId());
        mng.setDelAt(experienceForm.getDelAt());
    }

    /**
     * @param experienceForm
     * @return
     */
    @Transactional
    public Experience insert(ExperienceForm experienceForm, MultipartFile attachImgFl, MultipartFile attachedFile) throws Exception {

        if (attachImgFl.isEmpty() == false) {
            FileInfo fileInfo = FileUtilHelper.writeUploadedFile(attachImgFl, Constants.FOLDERNAME_EXPERIENCE);

            if (fileInfo.getFlNm() != null) {
                experienceForm.setImgFl(fileInfo.getFlNm());
            }
        }

        Experience experience = modelMapper.map(experienceForm, Experience.class);
        experience.setReadCnt(0);
        Experience save = experienceRepository.save(experience);

        if (attachedFile != null) {
            FileInfo fileInfo = FileUtilHelper.writeUploadedFile(attachedFile, Constants.FOLDERNAME_EXPERIENCE, FileUtilHelper.audioExt);
            if (fileInfo != null) {
                fileInfo.setDataPid(save.getId());
                TableNmType tblExperience = TableNmType.TBL_EXPERIENCE;
                fileInfo.setTableNm(tblExperience.name());
                fileInfo.setDvTy(FileDvType.ATTACH.name());

                fileInfoRepository.save(fileInfo);
            }
        }

        return save;
    }

    @Transactional
    public boolean update(ExperienceForm experienceForm, MultipartFile attachImgFl, MultipartFile attachedFile) {

        try {
            FileInfo fileInfo = new FileInfo();

            if (attachImgFl.isEmpty() == false) {
                fileInfo = FileUtilHelper.writeUploadedFile(attachImgFl, Constants.FOLDERNAME_EXPERIENCE);
            }

            Experience experience = experienceRepository.findById(experienceForm.getId()).orElseGet(Experience::new);
            experience.setTtl(experienceForm.getTtl());
            experience.setCn(experienceForm.getCn());
            if (fileInfo.getFlNm() != null) {
                experience.setImgFl(fileInfo.getFlNm());
            }
            experience.setCntntsDvTy(experienceForm.getCntntsDvTy());
            experience.setCntntsUrl(experienceForm.getCntntsUrl());
            experience.setDvCodePid(experienceForm.getDvCodePid());
            experience.setUpdPsId(experienceForm.getUpdPsId());
            experience.setUpdDtm(LocalDateTime.now());
            //return userRepository.save(account);

            if (attachedFile.isEmpty() == false) {
                TableNmType tblExperience = TableNmType.TBL_EXPERIENCE;
                FileInfo file = FileUtilHelper.writeUploadedFile(attachedFile, Constants.FOLDERNAME_EXPERIENCE);

                if (file != null) {
                    file.setDataPid(experienceForm.getId());
                    file.setTableNm(tblExperience.name());
                    file.setDvTy(FileDvType.ATTACH.name());

                    fileInfoRepository.deleteByData(file.getDataPid(), file.getTableNm(), file.getDvTy());
                    fileInfoRepository.save(file);
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean updateByReadCnt(ExperienceForm experienceForm) {

        try {
            Experience experience = experienceRepository.findById(experienceForm.getId()).orElseGet(Experience::new);
            experience.setReadCnt(experienceForm.getReadCnt());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long countForNotFileLoad(Long mberPid) {
        QExperience qExperience = QExperience.experience;
        QFileInfo qfIleInfo = QFileInfo.fileInfo;
        QActionLog qActionLog = QActionLog.actionLog;

        Long count = queryFactory
                .select(Projections.fields(Experience.class,
                        qActionLog.id
                ))
                .from(qActionLog)
                .where(qActionLog.cnctUrl.in(JPAExpressions.select(Expressions.asString("/pages/activity/experienceDetail/").concat(qExperience.id.stringValue()))
                        .from(qExperience)
                        .leftJoin(qfIleInfo).on(qExperience.id.eq(qfIleInfo.dataPid).and(qfIleInfo.tableNm.eq(TableNmType.TBL_EXPERIENCE.name())).and(qfIleInfo.id.isNull())))
                    .and(qActionLog.mberPid.eq(mberPid)))
                .fetchCount();
        return count;
    }
}
