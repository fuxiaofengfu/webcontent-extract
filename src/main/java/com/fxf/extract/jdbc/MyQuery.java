package com.fxf.extract.jdbc;

import org.apache.commons.collections.CollectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询
 */
public class MyQuery {

	public static List<Map<String,Object>> query(String sql, List<Object> params) throws Exception {
		Connection connection = MyConnection.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		if(CollectionUtils.isNotEmpty(params)){
			int index = 0;
			for(Object obj : params){
				preparedStatement.setObject(index,obj);
				index ++;
			}
		}
		ResultSet resultSet = preparedStatement.executeQuery();
		List<Map<String,Object>> result = new ArrayList<>();

		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		while (resultSet.next()){
			Map<String, Object> dataMap = new HashMap<>();
			for(int i=1;i<=columnCount;i++){
				dataMap.put(metaData.getColumnLabel(i),resultSet.getObject(i));
			}
			result.add(dataMap);
		}
		return result;
	}
}
