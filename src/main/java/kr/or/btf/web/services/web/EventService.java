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
import kr.or.btf.web.domain.web.Event;
import kr.or.btf.web.domain.web.FileInfo;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QEvent;
import kr.or.btf.web.domain.web.enums.FileDvType;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.repository.web.EventRepository;
import kr.or.btf.web.repository.web.FileInfoRepository;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.EventForm;
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
public class EventService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final FileInfoRepository fileInfoRepository;

    public Page<Event> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, (searchForm.getPageSize() == null ? Constants.DEFAULT_PAGESIZE : searchForm.getPageSize())); // <- Sort 추가

        QEvent qEvent = QEvent.event;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qEvent.id.desc();
        OrderSpecifier<String> orderSpecifierString = qEvent.ttl.asc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qEvent.delAt.eq("N"));

        if (searchForm.getSrchEventStDt() != null && searchForm.getSrchEventEdDt() != null) {
            if (!searchForm.getSrchEventStDt().isEmpty() && !searchForm.getSrchEventEdDt().isEmpty()) {
                builder
                        .and(qEvent.stYmd.goe(searchForm.getSrchEventStDt()).and(qEvent.edYmd.loe(searchForm.getSrchEventEdDt())));
            }
        }

        /*if (searchForm.getSrchStDt() != null && searchForm.getSrchEdDt() != null) {
            if (!searchForm.getSrchStDt().isEmpty() && !searchForm.getSrchEdDt().isEmpty()) {
                builder
                        .and(qEvent.regDtm.goe(Expressions.dateTimeTemplate(LocalDateTime.class,"{0}",searchForm.getSrchStDt()))
                                .and(qEvent.regDtm.loe(Expressions.dateTimeTemplate(LocalDateTime.class,"{0}",searchForm.getSrchEdDt()))));
            }
        }*/

        if (searchForm.getSorting() != null && !searchForm.getSorting().isEmpty()) {
            if (searchForm.getSorting().equals("old")) {
                orderSpecifier = qEvent.id.asc();
            } else if (searchForm.getSorting().equals("readCnt")) {
                orderSpecifier = qEvent.readCnt.longValue().desc();
            }
        }

        if (searchForm.getSrchField() != null && !searchForm.getSrchField().isEmpty()) {
            if (searchForm.getSrchField().equals("title")) {
                builder.and(qEvent.ttl.like("%" + searchForm.getSrchWord() + "%"));
            } else if (searchForm.getSrchField().equals("cn")) {
                builder.and(qEvent.cn.like("%" + searchForm.getSrchWord() + "%"));
            } else if (searchForm.getSrchField().equals("titleCn")) {
                builder.and(qEvent.ttl.like("%" + searchForm.getSrchWord() + "%").or(qEvent.cn.like("%" + searchForm.getSrchWord() + "%")));
            } else if (searchForm.getSrchField().equals("wrterNm")) {
                builder.and(qAccount.nm.like("%" + searchForm.getSrchWord() + "%"));
            }
        } else {
            if (searchForm.getSrchWord() != null && !searchForm.getSrchWord().isEmpty()) {
                builder.and(qEvent.ttl.like("%" + searchForm.getSrchWord() + "%").or(qEvent.cn.like("%" + searchForm.getSrchWord() + "%")));
            }
        }

        JPAQuery<Event> tempList = queryFactory
                .select(Projections.fields(Event.class,
                        qEvent.id,
                        qEvent.ttl,
                        qEvent.cn,
                        qEvent.imgFl,
                        qEvent.cntntsUrl,
                        qEvent.spotDtl,
                        qEvent.stYmd,
                        qEvent.edYmd,
                        qEvent.regDtm,
                        qAccount.nm.as("regPsNm")
                ))
                .from(qEvent)
                .leftJoin(qAccount).on(qAccount.loginId.eq(qEvent.regPsId))
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());
        if (searchForm.getSorting() != null && !searchForm.getSorting().isEmpty()) {
            if (searchForm.getSorting().equals("alphabetically")) {
                tempList.orderBy(orderSpecifierString);
            }
        }
        tempList.orderBy(orderSpecifier);

        QueryResults<Event> mngList = tempList.fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public List<Event> eventList(SearchForm searchForm) {

        QEvent qEvent = QEvent.event;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qEvent.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qEvent.delAt.eq("N"));

        if (searchForm.getSrchEventStDt() != null && searchForm.getSrchEventEdDt() != null) {
            if (!searchForm.getSrchEventStDt().isEmpty() && !searchForm.getSrchEventEdDt().isEmpty()) {
                builder
                        .and(qEvent.stYmd.goe(searchForm.getSrchEventStDt()).and(qEvent.edYmd.loe(searchForm.getSrchEventEdDt())));
            }
        }

        if (searchForm.getSrchStRegYear() != null && !searchForm.getSrchStRegYear().isEmpty()) {
            builder.and(qEvent.stYmd.substring(0,4).eq(searchForm.getSrchStRegYear()));
        }
        if (searchForm.getSrchStRegMonth() != null && !searchForm.getSrchStRegMonth().isEmpty()) {
            builder.and(qEvent.stYmd.substring(5,7).eq(searchForm.getSrchStRegMonth()));
        }

        if (searchForm.getSrchField() != null && !searchForm.getSrchField().isEmpty()) {
            if (searchForm.getSrchField().equals("title")) {
                builder.and(qEvent.ttl.like("%" + searchForm.getSrchWord() + "%"));
            } else if (searchForm.getSrchField().equals("cn")) {
                builder.and(qEvent.cn.like("%" + searchForm.getSrchWord() + "%"));
            } else if (searchForm.getSrchField().equals("titleCn")) {
                builder.and(qEvent.ttl.like("%" + searchForm.getSrchWord() + "%").or(qEvent.cn.like("%" + searchForm.getSrchWord() + "%")));
            } else if (searchForm.getSrchField().equals("wrterNm")) {
                builder.and(qAccount.nm.like("%" + searchForm.getSrchWord() + "%"));
            }
        } else {
            if (searchForm.getSrchWord() != null && !searchForm.getSrchWord().isEmpty()) {
                builder.and(qEvent.ttl.like("%" + searchForm.getSrchWord() + "%").or(qEvent.cn.like("%" + searchForm.getSrchWord() + "%")));
            }
        }

        List<Event> results = queryFactory
                .select(Projections.fields(Event.class,
                        qEvent.id,
                        qEvent.ttl,
                        qEvent.cn,
                        qEvent.imgFl,
                        qEvent.cntntsUrl,
                        qEvent.spotDtl,
                        qEvent.stYmd,
                        qEvent.edYmd,
                        qEvent.fxSeTy,
                        qEvent.regDtm,
                        qAccount.nm.as("regPsNm")
                ))
                .from(qEvent)
                .leftJoin(qAccount).on(qAccount.loginId.eq(qEvent.regPsId))
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return results;
    }

    public Event loadById(Long id) {
        Event load = eventRepository.findById(id).orElseGet(Event::new);

        return load;
    }

    public Event load(EventForm eventForm) {
        QEvent qEvent = QEvent.event;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qEvent.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        if (eventForm.getPrevNext() != null && eventForm.getPrevNext() != "") {
            if (eventForm.getPrevNext().equals("prev")) {
                builder.and(qEvent.id.lt(eventForm.getId()));
            }
            if (eventForm.getPrevNext().equals("next")) {
                orderSpecifier = qEvent.id.asc();
                builder.and(qEvent.id.gt(eventForm.getId()));
            }
        } else {
            builder.and(qEvent.id.eq(eventForm.getId()));
        }
        builder.and(qEvent.delAt.eq("N"));
        Event event = queryFactory
                .select(Projections.fields(Event.class,
                        qEvent.id,
                        qEvent.ttl,
                        qEvent.cn,
                        qEvent.stYmd,
                        qEvent.edYmd,
                        qEvent.spotDtl,
                        qEvent.statusType,
                        qEvent.imgFl,
                        qEvent.cntntsDvTy,
                        qEvent.cntntsUrl,
                        qEvent.readCnt,
                        qEvent.regPsId,
                        qEvent.regDtm,
                        qEvent.updPsId,
                        qEvent.updDtm,
                        qEvent.delAt,
                        qEvent.fxSeTy,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qEvent.regPsId)
                                                .and(qAccount.delAt.eq("N"))),
                                "regPsNm")
                ))
                .from(qEvent)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetchFirst();
        return event;
    }

    @Transactional
    public void delete(EventForm eventForm) {
        Event mng = this.loadById(eventForm.getId());

        mng.setUpdDtm(LocalDateTime.now());
        mng.setUpdPsId(eventForm.getUpdPsId());
        mng.setDelAt(eventForm.getDelAt());
    }

    /**
     * @param eventForm
     * @return
     */
    @Transactional
    public Event insert(EventForm eventForm, MultipartFile attachImgFl ,MultipartFile attachVideoFl, MultipartFile[] attachedFile) throws Exception {

        FileInfo fileInfo = FileUtilHelper.writeUploadedFile(attachImgFl, Constants.FOLDERNAME_EVENT);

        eventForm.setImgFl(fileInfo.getFlNm());

        Event event = modelMapper.map(eventForm, Event.class);
        event = eventRepository.save(event);

        TableNmType tblEvent = TableNmType.TBL_EVENT;

        if (attachVideoFl != null) {
            FileInfo videoFl = FileUtilHelper.writeUploadedFile(attachVideoFl, Constants.FOLDERNAME_EVENT);
            if (videoFl != null) {
                videoFl.setDataPid(event.getId());
                videoFl.setTableNm(tblEvent.name());
                videoFl.setDvTy(FileDvType.MAINVOD.name());
                fileInfoRepository.save(videoFl);
            }
        }

        if (attachedFile != null) {
            for (MultipartFile multipartFile : attachedFile) {
                if (multipartFile.isEmpty() == false) {
                    FileInfo attachFl = FileUtilHelper.writeUploadedFile(multipartFile, Constants.FOLDERNAME_EVENT);
                    if (attachFl != null) {
                        attachFl.setDataPid(event.getId());
                        attachFl.setTableNm(tblEvent.name());
                        attachFl.setDvTy(FileDvType.ATTACH.name());
                        fileInfoRepository.save(attachFl);
                    }
                }
            }
        }

        return event;
    }

    @Transactional
    public boolean update(EventForm eventForm, MultipartFile attachImgFl ,MultipartFile attachVideoFl, MultipartFile[] attachedFile) {

        try {
            FileInfo fileInfo = FileUtilHelper.writeUploadedFile(attachImgFl, Constants.FOLDERNAME_EVENT);

            Event event = eventRepository.findById(eventForm.getId()).orElseGet(Event::new);
            event.setTtl(eventForm.getTtl());
            event.setCn(eventForm.getCn());
            if (fileInfo != null) {
                event.setImgFl(fileInfo.getFlNm());
            }
            event.setCntntsDvTy(eventForm.getCntntsDvTy());
            event.setCntntsUrl(eventForm.getCntntsUrl());
            event.setStYmd(eventForm.getStYmd());

            event.setFxSeTy(eventForm.getFxSeTy());
            event.setSpotDtl(eventForm.getSpotDtl());

            event.setEdYmd(eventForm.getEdYmd());
            event.setUpdPsId(eventForm.getUpdPsId());
            event.setUpdDtm(LocalDateTime.now());

            TableNmType tblEvent = TableNmType.TBL_EVENT;
            if (attachVideoFl != null) {
                FileInfo videoFl = FileUtilHelper.writeUploadedFile(attachVideoFl, Constants.FOLDERNAME_EVENT);
                if (videoFl != null) {
                    videoFl.setDataPid(event.getId());
                    videoFl.setTableNm(tblEvent.name());
                    videoFl.setDvTy(FileDvType.MAINVOD.name());
                    fileInfoRepository.deleteByData(videoFl.getDataPid(), videoFl.getTableNm(), videoFl.getDvTy());
                    fileInfoRepository.save(videoFl);
                }
            }

            if (attachedFile != null) {
                for (MultipartFile multipartFile : attachedFile) {
                    if (multipartFile.isEmpty() == false) {
                        FileInfo attachFl = FileUtilHelper.writeUploadedFile(multipartFile, Constants.FOLDERNAME_EVENT);
                        if (attachFl != null) {
                            attachFl.setDataPid(event.getId());
                            attachFl.setTableNm(tblEvent.name());
                            attachFl.setDvTy(FileDvType.ATTACH.name());
                            fileInfoRepository.save(attachFl);
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
    public boolean updateByReadCnt(EventForm form) {
        try{
            Event event = eventRepository.findById(form.getId()).get();
            event.setReadCnt(form.getReadCnt());

            return true;
        }catch(Exception e){
            return false;
        }

    }
}
