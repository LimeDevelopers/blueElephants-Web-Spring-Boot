package kr.or.btf.web.test.web.domain;

import lombok.*;

import javax.persistence.*;


/*
* 작성자 : 김재일
* Lombok 어노테이션 정리
* @EqualsAndHashCode(of = "id") : 두 객체 간의 동등성, 동일성을 비교한다.
* 아래 어노테이션은 생성자를 자동으로 생성해준다.
* @NoArgsConstructor : 파라미터가 없는 생성자를 생성한다.
* @RequiredArgsConstructor : 추가 작업을 필요로 하는 필드에 대한 생성자를 생성한다.
* @AllArgsConstructor : 클래스에 존재하는 모든 필드에 대한 생성자를 자동으로 생성한다.
* */
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "tbl_user_menu")
public class Tbl_User_Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_pid")
    private Long id;

    @Column(name = "parnts_menu_pid")
    private Long pMid;

    @Column(name = "menu_nm")
    private String menuNm;

    @Column(name = "menu_url")
    private Long menuUrl;

    @Column(name = "newwin_at")
    private Long nAt;

    @Column(name = "menu_sn")
    private Long menuSn;

    @Column(name = "menu_dv")
    private String menuDv;

    @Column(name = "menu_dsc")
    private Long menuDsc;

    @Column(name = "reg_ps_id")
    private String regPsId;

    @Column(name = "reg_dtm")
    private String regDtm;

    @Column(name = "upd_ps_id")
    private Long uPsId;

    @Column(name = "reg_dtm")
    private String upDtm;

    @Column(name = "del_at")
    private String delAt;
}
