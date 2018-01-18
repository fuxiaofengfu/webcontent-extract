package com.fxf.extract.hbase;

import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HBaseConnectionUtil {

	public static Connection getConnection() {
		try {
			return ConnectionFactory.createConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取所有的表,逗号分割
	 * @return
	 */
	public static String tablesString(){
		Connection connection = null;
		Admin admin = null;
		try {
			connection = getConnection();
			admin = connection.getAdmin();
			NamespaceDescriptor[] namespaceDescriptors = admin.listNamespaceDescriptors();
			if(null == namespaceDescriptors || namespaceDescriptors.length<=0) return null;

			StringBuilder builder = new StringBuilder("hbase所有namespace及其tables如下:");
			for (NamespaceDescriptor namespace : namespaceDescriptors) {
				builder.append("\nnamespace:").append(namespace.toString());
				TableName[] tableNames = admin.listTableNamesByNamespace(namespace.getName());
				builder.append("\n\ttable:");
				for (TableName tn : tableNames) {
					builder.append(tn.getNameAsString()).append(",");
				}
			}
			return builder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			close(admin,connection);
		}
		return null;
	}

	/**
	 * 调用对象close方法
	 * @param objects
	 */
	public static void close(Object... objects){
		try {
			if(null == objects || objects.length<=0)return;
			for (Object object : objects) {
				if(null == object) continue;
				Method method = object.getClass().getMethod("close", null);
				if(null == method) continue;
				method.setAccessible(true); // 取消安全检查
				method.invoke(object);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
