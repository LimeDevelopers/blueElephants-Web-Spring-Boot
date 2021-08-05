package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.repository.web.CommonCodeRepository;
import kr.or.btf.web.repository.web.MngMenuRepository;
import kr.or.btf.web.web.form.MngMenuForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MngMenuService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final ModelMapper modelMapper;
    private final MngMenuRepository mngMenuRepository;
    private final CommonCodeRepository commonCodeRepository;

/*    public Integer getTotalCount(Menu vo) {
        return menuMapper.getTotalCount(vo);
    }*/

    public List<MngMenu> list(SearchForm form) {
        QMngMenu qMngMenu = QMngMenu.mngMenu;
        QCommonCode qcommonCode = QCommonCode.commonCode;

        OrderSpecifier<Long> orderSpecifier = qMngMenu.menuGroupCdPid.asc();
        OrderSpecifier<Integer> orderSpecifier2 = qMngMenu.menuSn.asc();

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qMngMenu.delAt.eq("N"));
        if (form.getSrchMnGbnCdPid() != null){
            builder.and(qMngMenu.menuGroupCdPid.eq(form.getSrchMnGbnCdPid()));
        }
        if (form.getSrchWord() != null){
            builder.and(qMngMenu.menuNm.eq(form.getSrchWord()));
        }

        List<MngMenu> list = queryFactory
                .select(Projections.fields(MngMenu.class,
                        qMngMenu.id,
                        qMngMenu.menuNm,
                        qMngMenu.menuUrl,
                        qMngMenu.newwinAt,
                        qMngMenu.menuGroupCdPid,
                        qMngMenu.menuSn,
                        qMngMenu.regDtm,
                        qMngMenu.updPsId,
                        qMngMenu.updDtm,
                        qMngMenu.delAt,
                        ExpressionUtils.as(
                                JPAExpressions.select(qcommonCode.codeNm)
                                        .from(qcommonCode)
                                        .where(qcommonCode.id.eq(qMngMenu.menuGroupCdPid)
                                                .and(qcommonCode.delAt.eq("N"))),
                                "menuGroupNm")
                ))
                .from(qMngMenu)
                .where(builder)
                .orderBy(orderSpecifier)
                .orderBy(orderSpecifier2)
                .fetch();

        return list;
    }

    public MngMenu load(MngMenuForm form) {
        return mngMenuRepository.findById(form.getId()).get();
    }

    @Transactional
    public boolean insert(MngMenuForm form) {
        try{
            MngMenu mngMenu = modelMapper.map(form, MngMenu.class);
            mngMenuRepository.save(mngMenu);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean update(MngMenuForm form) {
        try{
            MngMenu mngMenu = mngMenuRepository.findById(form.getId()).get();
            mngMenu.setMenuGroupCdPid(form.getMenuGroupCdPid());
            mngMenu.setMenuNm(form.getMenuNm());
            mngMenu.setMenuUrl(form.getMenuUrl());
            mngMenu.setNewwinAt(form.getNewwinAt());
            mngMenu.setMenuSn(form.getMenuSn());
            mngMenu.setUpdPsId(form.getUpdPsId());
            mngMenu.setUpdDtm(LocalDateTime.now());
            return true;
        }catch(Exception e){
            return false;
        }

    }

    @Transactional
    public boolean delete(List<MngMenuForm> menuFormList) {
        try{
            for(MngMenuForm form : menuFormList){
                MngMenu mngMenu = mngMenuRepository.findById(form.getId()).get();
                mngMenu.setUpdPsId(form.getUpdPsId());
                mngMenu.setUpdDtm(LocalDateTime.now());
                mngMenu.setDelAt("Y");
            }
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public List<MngMenu> gbnList(MngMenuForm form) {
//        QMngMenu qMngMenu = QMngMenu.mngMenu;
        QCommonCode qcommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qcommonCode.delAt.eq("N"));
        builder.and(qcommonCode.prntCodePid.eq(form.getMenuGroupCdPid()));
        builder.and(qcommonCode.delAt.eq("N"));

        List<CommonCode> list = queryFactory
                .select(Projections.fields(CommonCode.class,
                        qcommonCode.id,
                        qcommonCode.codeNm,
                        qcommonCode.codeSno
                ))
                .from(qcommonCode)
                .where(builder)
                .fetch();

        List<MngMenu> mngMenuList = new ArrayList<>();

        for(int i = 0; i<list.size(); i++){
            MngMenu vo = new MngMenu();
            vo.setId(list.get(i).getId());
            vo.setMenuNm(list.get(i).getCodeNm());
            vo.setMenuSn(list.get(i).getCodeSno());
            mngMenuList.add(vo);
        }

        return mngMenuList;
    }

    public List<MngMenu> lnbList(MngMenuForm form) {
        QMngMenu qMngMenu = QMngMenu.mngMenu;
        QMngMenuAuth qMngMenuAuth = QMngMenuAuth.mngMenuAuth;
        QCommonCode qcommonCode = QCommonCode.commonCode;

        OrderSpecifier<Integer> orderSpecifier = qMngMenu.menuSn.asc();

        BooleanBuilder builder = new BooleanBuilder();

        if(form.getUserPid() != null){
            builder.and(qMngMenuAuth.mberPid.eq(form.getUserPid()));
        }
        builder.and(qMngMenu.delAt.eq("N"));

        List<MngMenu> list = queryFactory
                .select(Projections.fields(MngMenu.class,
                        qMngMenu.id,
                        qMngMenu.menuGroupCdPid,
                        qMngMenu.menuNm,
                        qMngMenu.menuUrl,
                        qMngMenu.newwinAt,
                        qMngMenu.menuSn,
                        qMngMenu.regPsId,
                        qMngMenu.regDtm,
                        qMngMenu.updPsId,
                        qMngMenu.updDtm,
                        qMngMenu.delAt,
                        qMngMenuAuth.mberPid,
                        ExpressionUtils.as(
                                JPAExpressions.select(qcommonCode.codeNm)
                                        .from(qcommonCode)
                                        .where(qcommonCode.id.eq(qMngMenu.menuGroupCdPid)
                                                .and(qcommonCode.delAt.eq("N"))),
                                "menuGroupNm")
                ))
                .from(qMngMenu)
                .leftJoin(qMngMenuAuth).on(qMngMenuAuth.menuPid.eq(qMngMenu.id))
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return list;
    }

    /*public List<Menu> getListToAuth(Menu menu) {
        return menuMapper.getListToAuth(menu);
    }*/
}
