package kr.or.btf.web.config;

import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class RegisterSqlFunction extends MySQL5Dialect {
    public RegisterSqlFunction() {
        super();
        registerFunction("group_concat", new StandardSQLFunction("group_concat", StandardBasicTypes.STRING));
        registerFunction("date_format", new StandardSQLFunction("date_format", StandardBasicTypes.STRING));
        registerFunction("fn_getParentCodeList", new StandardSQLFunction("fn_getParentCodeList", StandardBasicTypes.STRING));
        registerFunction("fn_getMyAtnlcHour", new StandardSQLFunction("fn_getMyAtnlcHour", StandardBasicTypes.BIG_INTEGER));
        //registerFunction("fn_getParentCodeList", new SQLFunctionTemplate(StandardBasicTypes.STRING, "fn_getParentCodeList(?1,?2)"));
    }
}
