package com.fxf.extract.mr;

import com.fxf.extract.jdbc.MyQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * mapreduce 缓存类
 */
public class MrCache {

	private static final List<Map<String,Object>> cacheData = new ArrayList<>();

	public synchronized static List<Map<String,Object>> getCacheData(){
		if (CollectionUtils.isEmpty(cacheData)){
			try {
				StringBuilder builder = new StringBuilder();
				builder.append("select webhosts,content_xpath from crawler_content_xpath_use where status=0 for update");
				cacheData.addAll(MyQuery.query(builder.toString(), null));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return cacheData;
	}

	/**
	 * 根据host获取content的xpath
	 * @param host
	 * @return
	 */
	public static List<String> getContentXpathByHost(String host){
		if(StringUtils.isEmpty(host)){
			return null;
		}
		ArrayList<String> xpaths = new ArrayList<>();
		for(Map<String,Object> data : cacheData){
			String content_xpath = (String)data.get("content_xpath");
			if(StringUtils.isEmpty(content_xpath))continue;

			String webhosts = (String)data.get("webhosts");
			if(host.equals(webhosts))xpaths.add(content_xpath);
		}
		return xpaths;
	}
}
