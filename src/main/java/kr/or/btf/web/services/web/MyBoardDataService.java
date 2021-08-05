package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.MyBoardData;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QFileInfo;
import kr.or.btf.web.domain.web.QMyBoardData;
import kr.or.btf.web.repository.web.FileInfoRepository;
import kr.or.btf.web.repository.web.MyBoardDataRepository;
import kr.or.btf.web.web.form.MyBoardDataForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MyBoardDataService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final ModelMapper modelMapper;
    private final MyBoardDataRepository myBoardDataRepository;
    private final FileInfoRepository fileInfoRepository;

    public Page<MyBoardData> list(Pageable pageable, MyBoardDataForm myBoardDataForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QMyBoardData qMyBoardData = QMyBoardData.myBoardData;
        QAccount qAccount = QAccount.account;
        QFileInfo qFileInfo = QFileInfo.fileInfo;

        OrderSpecifier<Long> orderSpecifier = qMyBoardData.id.desc();

        BooleanBuilder builder = new BooleanBuilder();

        QueryResults<MyBoardData> mngList = queryFactory
                .select(Projections.fields(MyBoardData.class,
                        qMyBoardData.id,
                        qMyBoardData.regDtm,
                        qMyBoardData.mberPid,
                        qMyBoardData.dataPid
                ))
                .from(qMyBoardData)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public MyBoardData load(MyBoardDataForm myBoardDataForm) {
        MyBoardData myBoardData = myBoardDataRepository.findById(myBoardDataForm.getId()).orElseGet(MyBoardData::new);

        return myBoardData;
    }

    public MyBoardData loadByDataPidAndMberPid(MyBoardDataForm myBoardDataForm) {
        MyBoardData myBoardData = myBoardDataRepository.findByDataPidAndMberPid(myBoardDataForm.getDataPid(), myBoardDataForm.getMberPid());
        return myBoardData;
    }

    @Transactional
    public boolean deleteByMberPidAndDataPid(MyBoardDataForm myBoardDataForm) {
        try {
            myBoardDataRepository.deleteByDataPidAndMberPid(myBoardDataForm.getDataPid(), myBoardDataForm.getMberPid());
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Transactional
    public boolean insert(MyBoardDataForm form) {
        try{
            MyBoardData myBoardData = modelMapper.map(form, MyBoardData.class);
            myBoardData.setRegDtm(LocalDateTime.now());
            MyBoardData save = myBoardDataRepository.save(myBoardData);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    @Transactional
    public boolean update(MyBoardDataForm form) {
        try{
            MyBoardData myBoardData = myBoardDataRepository.findById(form.getId()).get();
            myBoardData.setRegDtm(form.getRegDtm());
            return true;
        }catch(Exception e){
            return false;
        }

    }
}
