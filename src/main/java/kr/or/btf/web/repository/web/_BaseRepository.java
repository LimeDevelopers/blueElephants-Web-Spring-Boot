package kr.or.btf.web.repository.web;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

// rollbackFor = AppCheckException.class 설정을 해도 RuntimeException 상속이 아니면 안됨.
//@Transactional(readOnly = true, rollbackFor = { RuntimeException.class, Error.class, AppCheckException.class } ) 
//protected Logger log = LoggerFactory.getLogger(getClass()); 
//public abstract class _BaseService extends Base {
// repository 서비스 상속을 위한 일단 extends를 제외함. 
public abstract class _BaseRepository {

	protected Logger log = LoggerFactory.getLogger(getClass());

	@PersistenceContext
	protected EntityManager em;

	
	/**
	 * 일반sql 조회한 결과를 List<Map>형식으로 리턴함. 
	 * @param qry
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public List<Map<String, Object>> listMapByNativeSql(String qry) {

        Query query = em.createNativeQuery(qry);
        
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        @SuppressWarnings("unchecked")
		List<Map<String, Object>> rows = query.getResultList();
        
        if ( log.isDebugEnabled() ) {
        	if ( rows != null && rows.size() > 0 ) log.debug("row keyset:{}", rows.get(0).keySet());
        }
        
		return rows;
	}

	/**
	 * 숫자리스트를 콤마형식으로 변환. (sql in 등에 활용) 
	 * @param longList
	 * @return
	 */
	public String convertComma(Long[] longList) {
		
		String sqlIn = Arrays.toString(longList).replace("[","").replace("]","");
		
		return sqlIn;
	}

	/*
	 * public List<Object[]> listMapByNativeQuery(String qry) {
	 * 
	 * 
	 * Query query = em.createNativeQuery(qry);
	 * 
	 * return query.getResultList();
	 * 
	 * javax.persistence.Query query = em.createNativeQuery(qry, Tuple.class);
	 * 
	 * XXXX lst = query.getResultStream().collect( Collectors.toMap(keyMapper,
	 * valueMapper) )
	 * 
	 * 
	 * return null; }
	 */

	/*
	 * //https://brixomatic.wordpress.com/2016/07/14/returning-the-result-of-a-jpa-
	 * native-query-as-a-simple-map-or-pojo/ private Map<String, Object>
	 * getColumNameToValueMapFromRowValueArray(final Object[] rowValueArray, final
	 * Map<String, Integer> columnNameToIndexMap) { //
	 * stream().collect(toMap(keyFunct, valueFunct)...) will not accept "null"
	 * values, so we do it this way: final Map<String, Object> result = new
	 * LinkedHashMap<>(); columnNameToIndexMap.entrySet().forEach(nameToIndexEntry
	 * -> result.put(nameToIndexEntry.getKey(),
	 * rowValueArray[nameToIndexEntry.getValue()])); return result; } private
	 * List<Map<String, Object>> asListOfMaps(final List<Object[]>
	 * queryResultAsListOfObjectArrays, final Map<String, Integer>
	 * columnNameToIndexMap) { final Function<Object[], Map<String, Object>>
	 * rowValueArrayToColumnNameToValueMap = rowValueArray -> { return
	 * getColumNameToValueMapFromRowValueArray(rowValueArray, columnNameToIndexMap);
	 * };
	 * 
	 * return queryResultAsListOfObjectArrays.stream().collect(mapping(
	 * rowValueArrayToColumnNameToValueMap, toList())); }
	 */
}
