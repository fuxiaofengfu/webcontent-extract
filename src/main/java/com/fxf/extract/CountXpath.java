package com.fxf.extract;

import com.fxf.extract.jdbc.MyQuery;
import com.fxf.extract.jdbc.MyTransactionalDML;
import org.apache.commons.collections.CollectionUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 统计xpath比率达到70%以上的xpath
 */
public class CountXpath {
	//按照xpath的比例取出正文xpath
	private static final float CONTENT_XPATH_RATE = 0.7f;
	//按照host统计的最小行数,低于该行数不计算比例
    private static final int COUNT_MIN_ROW = 3;

	public synchronized static void countXpath(){

		try {

			StringBuilder sql = new StringBuilder();
			sql.append("insert into crawler_content_xpath_use(`webhosts`,`content_xpath`,`create_time`) ");
			sql.append("values");

			List<Map<String, Object>> qualifiedXpath = getQualifiedXpath();
			if(CollectionUtils.isEmpty(qualifiedXpath))return;

			Date now = new Date();
			List<Object> params = new ArrayList<>();
			for (Map<String,Object> data : qualifiedXpath){
				sql.append("(?,?,?),");
				params.add(data.get("webhosts"));
				params.add(data.get("content_xpath"));
				params.add(now);
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(" ON DUPLICATE KEY UPDATE content_xpath=content_xpath");
			MyTransactionalDML.executeDML(sql.toString(),params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static List<Map<String, Object>> getQualifiedXpath(){
		StringBuilder sql = new StringBuilder();
		sql.append("select t3.webhosts,t3.content_xpath from ( ");
		sql.append("select round(t2.xpathnum / t1.total,2) xpath_rate,t2.webhosts,t2.content_xpath from ");
		sql.append("(select sum(1) total,webhosts from crawler_content_xpath where status=0 group by webhosts) t1 ");
		sql.append("inner join ");
		sql.append("(select webhosts,content_xpath,count(1) xpathnum from crawler_content_xpath where status=0 group by webhosts,content_xpath) t2 ");
		sql.append("on t1.webhosts = t2.webhosts and t1.total >= ? ");
		sql.append(") t3 where t3.xpath_rate>= ? ");
		List<Object> params = new ArrayList<>();
		params.add(COUNT_MIN_ROW);
		params.add(CONTENT_XPATH_RATE);
		try {
			List<Map<String, Object>> result = MyQuery.query(sql.toString(), params);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		countXpath();
	}
}
