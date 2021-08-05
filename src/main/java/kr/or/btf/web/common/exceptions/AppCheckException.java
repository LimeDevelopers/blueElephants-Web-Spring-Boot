package kr.or.btf.web.common.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 반드시 확인을 해야할 오류. 
 * Exception을 상속받으면 service에서 오류 발생시 rollback 되지 않아 RruntimeException으로 변경
 * BaseService에서 rollbackFor = { RuntimeException.class, Error.class, AppCheckException.class } 설정을 해도 안됨.
 * RuntimeException이 답인가..?
 * @author user
 *
 */
//public class AppCheckException extends Exception {
public class AppCheckException extends RuntimeException {

	private static final long serialVersionUID = -7322897902204372630L;

	protected Logger log = LoggerFactory.getLogger(getClass()); 

	public AppCheckException(String message) {
		super(message); 
	}
}
