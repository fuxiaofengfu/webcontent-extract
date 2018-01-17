package com.fxf.extract.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * TODO
 */
public class MyQuery {

	public static ResultSet query() throws Exception {
		Connection connection = MyConnection.getConnection();
		String sql = "select * from config_dict";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		ResultSet resultSet = preparedStatement.executeQuery();
		return resultSet;
	}

	public static void main(String[] args) throws Exception {
		ResultSet query = query();
		System.out.println();
	}
}
