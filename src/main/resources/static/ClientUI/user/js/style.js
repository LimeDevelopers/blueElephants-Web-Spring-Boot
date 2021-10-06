$(document).ready(function(){

	/*
	* 수정일 : 21.08.09
	* 수정자 : 김재일
	* 수정내용 :
	* 1. fixed 수정, dp_none 클래스 주입,삭제 (218 line)
	* 2. window.location.href 라우팅 문자열을 가져와서 인덱스를 제외한 나머지 페이지에서 fixed가 발생되지않도록 수정함.
	* */

	// 추가
	var uri = window.location.href;
	var route = uri.charAt(uri.length-1);

	if(route != '/'){
		$('#header').addClass('fixed')
		$('.mo_top_nav_wrap').addClass('dp_none')
	}

	//말줄임표
	$('.eps2').ellipsis({lines:2,responsive:true});
	$('.eps3').ellipsis({lines:3,responsive:true});

	//상단으로 스크롤
	$('.btn_top').on('click', function(){
		$('html, body').stop().animate({'scrollTop':0},500)
	})

	//검색버튼
	$('.btn_search').on('click', function(){
		$('.user_login').removeClass('active')
		$('.btn_search').toggleClass('active')
		$('.search_form').toggleClass('active')
	})

	//로그인 후 사용자 메뉴
	$('#header .btn_user').on('click',function(){
		$(this).parent('.user_login').toggleClass('active')
		$('.btn_search, .search_form').removeClass('active')
	})

	//마우스 오버 & 아웃시 메뉴
	$('#header .mnu > .mnu_list').on('mouseover',function(){
		$('#header').addClass('over');
		$('#header .mnu .sub_list, #header .depth').slideDown();
	})
	$('#header h1, #header .aside').on('mouseover',function() {
		$('#header').removeClass('over');
		$('#header .mnu .sub_list, #header .depth').slideUp();
	})
	$('#header').on('mouseleave',function() {
		$('#header').removeClass('over');
		$('#header .mnu .sub_list, #header .depth').stop().slideUp();
	})

	//모바일 햄버거 메뉴
	// $('.mo_ham').on('click', function(){
	// 	$('html, body').css({'overflow':'hidden'})
	// 	$(this).parents('#header').addClass('active')
	// })
	// $('.mnu_area .btn_close').on('click', function(){
	// 	$('html, body').removeAttr('style')
	// 	$(this).parents('#header').removeClass('active')
	// })

	//20210830_모바일 햄버거 메뉴
	$('.mo_ham').on('click', function(){
		// $('html, body').css({'overflow':'hidden'})
		$('.menu_modal').css({'overflow':'hidden'})
		$(this).parents('#gnb').parents('#header').siblings('#container').children('.menu_modal').addClass('active')
		$('body').addClass('activehamburger')
	})
	$('.btn_inner .close_btn').on('click', function(){
		$('.menu_modal').removeClass('active')
		$('body').removeClass('activehamburger')
	})


	//20210830_모바일 메뉴바 서브메뉴
	$('.depths_02 li').on('click', function(){
		$(this).addClass('m_on')
		$(this).siblings().removeClass('m_on')
		$(this).children('ul.subm_side').addClass('m_on')
		$(this).siblings().children('ul.subm_side').removeClass('m_on')
	})

	//20210831_서브페이지 슬라이드메뉴
	// $('.sm_tit h3').on('click', function(){
	// 	$(this).addClass('sd_on')
	// 	$(this).parent().parent().siblings().children('.sm_tit').children('h3').removeClass('sd_on')
	// 	$(this).parent().siblings('ul.slide_sub').addClass('sd_on')
	// 	$(this).parent().parent().siblings().children('ul.slide_sub').removeClass('sd_on')
	// })

	//20210901_능력카드제작소 제작요청 모달창
	$('.card_qr .card_btn').on('click', function(){
		$('.card_modal').addClass('.ml_active')
	})


	//footer 모바일 열림&닫힘 활성화
	var footBtn = $('#footer > .bottom .info_area > .txt_area .btn_mo_open');
	footBtn.on('click',function(){
		footBtn.parents('.txt_area').toggleClass('active');
	})

	//캘린더
	/*$('.form_date').datetimepicker({
         timepicker:false,
         format:'Y-m-d',
         scrollMonth:false
     });*/

	/*$('.form_datetime').datetimepicker({
       format:'Y-m-d H:i',
       scrollMonth:false
   });

   $('.form_time').datetimepicker({
       datepicker:false,
       format:'H:i',
       scrollMonth:false
   });*/

	//날짜 기간 선택
	$('.btn_term').each(function(){
		$(this).on('click',function(){
			$(this).addClass('active').siblings('.btn_term').removeClass('active')
		})
	})

	//ie input type number
	$('input[type=number]').on('keyup', function(){
		$(this).val($(this).val().replace(/[^0-9]/g, ''));
	});

	//nav tab
	$('.nav_tab > li').each(function(){
		$(this).find('a').on('click',function(){
			$(this).parent().addClass('active').siblings().removeClass('active');
		})
		$(this).find('.nav_tab_close').on('click',function(){
			$(this).parent().remove();
		})
	})

	//tab
	$('.tab').each(function(){
		var tab = $(this);
		tab.find('li').each(function(){
			var li = $(this),
				idx = li.index();
			li.find('a').on('click', function(e){
				if($(this).attr('href') == '#'){
					e.preventDefault();
				}
				li.addClass('active').siblings().removeClass('active');

				if(tab.parent('div').hasClass('tab_area')){
					tab.siblings('.tab_cont').find('.cont').eq(idx).addClass('active').siblings().removeClass('active');
				}
			})
		})
	})

	//icon tab
	$('.tab_ico_area .tab_type_ico > li').each(function(){
		var tabLi = $(this), tabIdx = tabLi.index(), tabArea = tabLi.parents('.tab_ico_area');
		tabLi.find('a').on('click', function(e){
			var tit = $(this).text();

			if($(this).attr('href') == '#'){
				e.preventDefault();
			}

			/* tabLi.addClass('active').siblings().removeClass('active');
			tabArea.children('.tit').text(tit)
			tabArea.children('.tab_cont').find('.cont').eq(tabIdx).addClass('active').siblings().removeClass('active'); */

			//mo
			if(tabArea.hasClass('active')){
				tabArea.removeClass('active').find('.tab_type_ico').stop().slideUp()
			}
		})
	})

	$('.tab_ico_area .tit').on('click', function(){
		$(this).parent().toggleClass('active').find('.tab_type_ico').stop().slideToggle()
	})

	//레이어 팝업 딤 클릭 시 팝업 닫기
	$('.layer_popup').each(function(){
		$(this).children('.dim').on('click', function(){
			$(this).parent('.layer_popup').removeClass('active');
			$('html').removeAttr('style');
		})
	})

	//이메일 주소에서 직접입력 선택시 입력창 출력
	$('.email_form').each(function(){
		$(this).find('select').on('change',function(){
			if($(this).val() == '직접입력'){
				$(this).parents('.email_form').find('.email_inp').addClass('active').val('').focus()
			}else{
				$(this).parents('.email_form').find('.email_inp').removeClass('active').val('')
			}
		})
	})

	// $('select').niceSelect();

	//select
	var i = 0;
	$('.select').each(function(){
		$(this).find('select').on('change',function(){
			var selected = $(this).children(':selected'),
				selectedTxt = selected.text();
			if(selected.length >= 2){
				$(this).siblings('label').text(selected.length+'개 선택됨');
			}else{
				$(this).siblings('label').text(selectedTxt);
			}
		});
	});

	//부서 토글
	fn_toggleDept();

	//커스텀 스크롤바
	$('.scrollbar').each(function(index, el){
		new SimpleBar(el,{
			autoHide:false
		})
	});

	//file_form 스크립트 적용
	setFileForm();
	winSize(); termForm();

	$(window).resize(function(){
		winSize(); termForm();
	})

	$(window).scroll(function(){
		var bodyH = $('body').height(),
			winH = $(window).height(),
			sct = $(window).scrollTop(),
			scb = bodyH - winH - sct,
			footerH = $('#footer').height(),
			sec01 = $('.section01').height();

		//header
		if(sct > 21){
			if(route == '/'){
				$('#header').addClass('fixed')
				$('.mo_top_nav_wrap').addClass('dp_none')
			}
		}else{
			if(route == '/'){
				$('#header').removeClass('fixed')
				$('.mo_top_nav_wrap').removeClass('dp_none')
			}else{
				$('#header').addClass('fixed')
				$('.mo_top_nav_wrap').addClass('dp_none')
			}

		}

		//퀵메뉴
		if(sct >= sec01 + 138){
			$('#quick').addClass('fixed')
		}else{
			$('#quick').removeClass('fixed')
		}

		//top 버튼
		if(scb <= footerH + 50){
			$('.btn_top').addClass('absolute')
		}else{
			$('.btn_top').removeClass('absolute')
		}
	})

	// 퀵메뉴 높이값
	$(window).on('load resize',function(){
		var sec01 = $('.section01').height();
		$('#quick').css('top', sec01 + 233);
	})
})

//term
function termForm(){
	$('.term_form').each(function(){
		var tdW = $(this).parent('td').width();

		if(tdW <= 296 && tdW >= 282){
			$(this).addClass('w100p')
		}else{
			$(this).removeClass('w100p')
		}

		if(tdW < 283){
			$(this).addClass('full')
		}else{
			$(this).removeClass('full')
		}
	})
}


//해상도에 따른 스크립트
function winSize(){
	var winW = $(window).width();
	if(winW > 1023){ //PC
		//모바일 햄버거 관련
		$('html, body').removeAttr('style')
		$('#header').removeClass('active')
		$('.mo_mnu_list > li').removeClass('active').find('.depth').removeAttr('style')

		var position = Math.ceil($('.user_login').position().left),
			asideW = Math.ceil($('.aside').width());
		$('.search_form').css({'right':asideW - position - 103})

		//20210831_서브페이지 슬라이드메뉴
		$('.sm_tit h3').on('click', function () {
			$(this).addClass('sd_on')
			$(this).parent().parent().siblings().children('.sm_tit').children('h3').removeClass('sd_on')
		})


	}else{ //MO
		$('#header .user_login').removeClass('active')

		$('.mnu_list > li').each(function(){
			var mnuLiIdx = $(this).index()
			$(this).children('a').on('click', function(e){
				e.preventDefault();
				$(this).parent('li').addClass('active').siblings('li').removeClass('active').parents('.mnu').find('.mo_mnu').eq(mnuLiIdx).addClass('active').siblings('.mo_mnu').removeClass('active')
			})
		})

		$('.mo_mnu_list > li').each(function(){
			$(this).children('.arr').on('click', function(){
				$(this).next('.depth').stop().slideToggle().parent('li').toggleClass('active').siblings('li').removeClass('active').find('.depth').stop().slideUp()
			})
		})

		$('.search_form').removeAttr('style')


		$('.sm_tit h3').on('click', function () {
			$(this).addClass('sd_on')
			$(this).parent().parent().siblings().children('.sm_tit').children('h3').removeClass('sd_on')
			$(this).parent().siblings('ul.slide_sub').toggleClass('sd_on')
			$(this).parent().parent().siblings().children('ul.slide_sub').removeClass('sd_on')
		})

		//모바일 슬라이드서브메뉴
		$('.slide_menu').slick({
			infinite: true,
			slidesToShow: 4,
			slidesToScroll: 1
		});
	}

	//검색버튼
	$('.btn_search').on('click', function(){
		if(winW > 1023){
			$('html, body').removeAttr('style')
		}else{
			if($('.btn_search').hasClass('active')){
				$('html, body').css({'overflow':'hidden'})
			}else{
				$('html, body').removeAttr('style')
			}
		}
	})

	//thumb list
	$('.thumb_list > li').each(function(){
		var imgW = Math.ceil($(this).width() * 0.59);
		var imgDataW = Math.ceil($(this).width() * 1);

		$(this).find('.thumb > img').css({'height':imgW})
		if($(this).parent().hasClass('dataroom')){
			$(this).find('.thumb > img').css({'height':imgDataW})
		}
	})
}

//file_form 스크립트 적용
function setFileForm(className) {
	className = className ? className : '.file_form';
	$(className).each(function(){
		var fileForm =$(this),
			filePlaceholder = fileForm.find('.file_name').text();

		//첨부 파일 삭제
		fileForm.find('.btn_file_del').on('click',function(){
			var agent = navigator.userAgent.toLowerCase();
			if((navigator.appName == 'Netscape' && navigator.userAgent.search('Trident') != -1) || (agent.indexOf("msie") != -1) ){ //ie
				fileForm.find('input').replaceWith($('.file_form input').clone(true));
			}else{//other browser
				fileForm.find('input').val('');
				fileForm.removeClass('active').find('.file_name').text(filePlaceholder)
			}
		});

		//첨부 파일 multilfile에서 label 클릭 시 첨부 파일 업로드
		fileForm.find('label').on('click',function(){
			fileForm.find('.MultiFile-wrap input').each(function(){
				if(!$(this).attr('style')){
					$(this).click()
				}
			})
		})
	});
}

//첨부파일
function uploadChange(file){
	var node = file.previousElementSibling;
	if(node.className == 'file_name' || node.className == 'file_name active'){
		node.innerHTML = file.value;
	}
	file.parentElement.classList.add('active')
}

//글자수 제한
function maxLengthCheck(object){
	if(object.value.length > object.maxLength){
		object.value = object.value.slice(0, object.maxLength);
	}
}

//레이어 팝업 열기
function layerShow(ele){
	/* var top = $(window).scrollTop() + 20;
	if(top < 50){
	    top = 50;
	}
	$('#'+ele).addClass('active').css({'top':top+'px'}) */
	$('#'+ele).addClass('active')
}
//레이어 팝업 닫기
function layerHide(ele){
	$('#'+ele).removeClass('active');
}

//윈도우 팝업
function winPop(url, title, w, h){
	var top = (screen.height/2)-(h/2),
		left = (screen.width/2)-(w/2);
	return window.open(url, title, 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=no, copyhistory=no, width='+w+', height='+h+', top='+top+', left='+left);
}

//ifram 콘텐츠 높이
function autoResize(obj){
	var newheight;
	var newwidth;
	if(obj.contentDocument){
		newheight = obj.contentDocument.documentElement.scrollHeight+30;
		newwidth = obj.contentDocument.documentElement.scrollWidth+30;
	}else{
		newheight=obj.contentWindow.document.body.scrollHeight+30;
		newwidth=obj.contentWindow.document.body.scrollWidth+30;
	}
	obj.height= newheight + 'px';
	obj.width= newwidth + 'px';
}
function isNumeric(num, opt) {
	// 좌우 trim(공백제거)을 해준다.
	num = String(num).replace(/^\s+|\s+$/g, "");

	if (typeof opt == "undefined" || opt == "1") {
		// 모든 10진수 (부호 선택, 자릿수구분기호 선택, 소수점 선택)
		var regex = /^[+\-]?(([1-9][0-9]{0,2}(,[0-9]{3})*)|[0-9]+){1}(\.[0-9]+)?$/g;
	} else if (opt == "2") {
		// 부호 미사용, 자릿수구분기호 선택, 소수점 선택
		var regex = /^(([1-9][0-9]{0,2}(,[0-9]{3})*)|[0-9]+){1}(\.[0-9]+)?$/g;
	} else if (opt == "3") {
		// 부호 미사용, 자릿수구분기호 미사용, 소수점 선택
		var regex = /^[0-9]+(\.[0-9]+)?$/g;
	} else {
		// only 숫자만(부호 미사용, 자릿수구분기호 미사용, 소수점 미사용)
		var regex = /^[0-9]$/g;
	}

	if (regex.test(num)) {
		num = num.replace(/,/g, "");
		return isNaN(num) ? false : true;
	} else {
		return false;
	}
}

function fn_getDate(dateType, addCount, dateFormat){
	var defaultFormat = 'yyyyMMdd';
	var date = new Date();

	dateType = dateType ? dateType : "d";
	addCount = addCount ? addCount : 0;
	dateFormat = dateFormat ? dateFormat : defaultFormat;

	if (dateType != 'y' && dateType != 'm' && dateType != 'd') {
		return false;
	}
	dateType = dateType.toLowerCase();	// 소문자로 변경

	if (!isNumeric(addCount)) {
		return false;
	}
	// addCount 정규식 체크

	//값 체크 끝나고 날짜 계산 로직 Start
	if (dateType == 'y') {
		date.setFullYear(date.getFullYear()+addCount);
	} else if (dateType == 'm') {
		date.setMonth(date.getMonth()+addCount);
	} else if (dateType == 'd') {
		date.setDate(date.getDate()+addCount);
	}
	return date.format(dateFormat);
}

//date format 함수  : Date 내장 객체에 format함수 추가
Date.prototype.format = function(f) {
	if (!this.valueOf()) return " ";

	var weekName = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
	var d = this;

	return f.replace(/(yyyy|yy|MM|dd|E|hh|mm|ss|a\/p)/gi, function($1) {
		switch ($1) {
			case "yyyy": return d.getFullYear();
			case "yy": return (d.getFullYear() % 1000).zf(2);
			case "MM": return (d.getMonth() + 1).zf(2);
			case "dd": return d.getDate().zf(2);
			case "E": return weekName[d.getDay()];
			case "HH": return d.getHours().zf(2);
			case "hh": return ((h = d.getHours() % 12) ? h : 12).zf(2);
			case "mm": return d.getMinutes().zf(2);
			case "ss": return d.getSeconds().zf(2);
			case "a/p": return d.getHours() < 12 ? "오전" : "오후";
			default: return $1;
		}
	});};

//한자리일경우 앞에 0을 붙여준다.
String.prototype.string = function(len){
	var s = '', i = 0;
	while (i++ < len) { s += this; }
	return s;
};
String.prototype.zf = function(len){return "0".string(len - this.length) + this;};
Number.prototype.zf = function(len){return this.toString().zf(len);};

function fn_toggleDept(select) {
	select = select ? select : ".list_chk";
	$(select+' > dt').each(function(){
		$(this).on('click',function(){
			$(this).next('dd').stop().slideToggle()
		})
	})
}

function emailCheck(email) {
	var regExp = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/;

	if (regExp.test(email.val()) == false) {
		// 이메일 형식이 알파벳+숫자@알파벳+숫자.알파벳+숫자 형식이 아닐경우
		//alert("이메일형식이 올바르지 않습니다.");
		email.after('<p class="err emph">이메일형식이 올바르지 않습니다.</p>');
		return false;
	}
	return true;
}

function loginIdCheck(loginId, errTgt) {
	var errTgt;
	if (errTgt) {
		errTgt = errTgt;
	} else {
		errTgt = loginId;
	}
	errTgt.siblings('.err.emph').remove();
	var regExp = /[a-z0-9]{6,12}/;
	if ($.trim(loginId.val()) == '') {
		errTgt.after('<p class="err emph">아이디는 필수 값입니다.</p>');
		loginId.focus();
		return false;
	}

	if (regExp.test(loginId.val()) == false) {
		//alert("아이디는 영문 소문자, 숫자를 포함해서 6~12자리 이내로 입력해주세요.");
		errTgt.after('<p class="err emph">아이디는 영문 소문자, 숫자를 포함해서 6~12자리 이내로 입력해주세요.</p>');
		loginId.focus();
		return false;
	}
	return true;
}

function pwdCheck(pwd, errTgt) {
	var errTgt;
	if (errTgt) {
		errTgt = errTgt;
	} else {
		errTgt = pwd;
	}
	errTgt.siblings('.err.emph').remove();
	var regExp = /^(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9])(?=.*[0-9]).{8,16}$/;

	if ($.trim(pwd.val()) == '') {
		errTgt.after('<p class="err emph">비밀번호는 필수 값입니다.</p>');
		pwd.focus();
		return false;
	}

	if (regExp.test(pwd.val()) == false) {
		//alert("비밀번호는 특수문자를 포함하여 8~16자리 이내로 입력해주세요.");
		errTgt.after('<p class="err emph">비밀번호는 특수문자를 포함하여 8~16자리 이내로 입력해주세요.</p>');
		return false;
	}
	return true;
}

function moblphonCheck(moblphon) {
	moblphon.siblings('.err.emph').remove();
	var regExp = /^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})([0-9]{3,4})([0-9]{4})$/;

	if ($.trim(moblphon.val()) == '') {
		moblphon.after('<p class="err emph">휴대전화번호는 필수 값입니다.</p>');
		moblphon.focus();
		return false;
	}

	if (regExp.test(moblphon.val()) == false) {
		//alert("휴대전화번호 형식이 올바르지 않습니다.");
		moblphon.after('<p class="err emph">휴대전화번호 형식이 올바르지 않습니다.</p>');
		return false;
	}
	return true;
}

function stdntNoCheck(obj, e) {
	obj.siblings('.err.emph').remove();
	e = e || window.event;
	var charCode = (typeof e.which == "undefined") ? e.keyCode : e.which;
	var charStr = String.fromCharCode(charCode);

	if (!(0 < obj.value && obj.value < 100)) {
		obj.value = '';
		//alert('1~99 사이의 숫자만 입력가능합니다.');
		obj.after('<p class="err emph">1~99 사이의 숫자만 입력가능합니다.</p>');
		return;
	}
	if (!charStr.match(/^[1-9]+$/))
		e.preventDefault();
}
<!--2021/09/21 kjw 개인정보활용동의서-->
$('.info_accordion .agree_inner ul').click(function(){
	$(this).toggleClass('agree_on')
})

//20210913_소개_예방교육 개인정보활용동의서
$('.agree_item_01 .agree_top u').click(function(){
	$('.agr_cont1').toggleClass('agree_on')
})
$('.agree_item_02 .agree_top u').click(function(){
	$('.agr_cont2').toggleClass('agree_on')
})

