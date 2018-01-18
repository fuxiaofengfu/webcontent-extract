package com.fxf.extract.hbase;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.CollectionUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HBaseUtil {

	public static void saveBatchData(TableName tableName, List<Map<String, String>> columns) {
		Connection connection = null;
		Admin admin = null;
		try {

			if (CollectionUtils.isEmpty(columns)) return;

			connection = HBaseConnectionUtil.getConnection();
			admin = connection.getAdmin();
			if (!admin.tableExists(tableName)) {
				throw new RuntimeException(MessageFormat.format("表{0}不存在",tableName.getNameAsString()));
			}
			Table table = connection.getTable(tableName);

			List<Put> puts = new ArrayList<>();
			for (Map<String, String> map : columns) {
				String row = map.get("row");
				String family = map.get("family");
				String qualifier = map.get("qualifier");
				String value = map.get("value");
				Put put = new Put(Bytes.toBytes(row));
				put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
				puts.add(put);
			}
			table.put(puts);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			HBaseConnectionUtil.close(connection, admin);
		}
	}

	public static void createTable(Admin admin, TableName tableName, String... families) throws IOException {

		HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
		for (String family : families) {
			HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(family);
			hColumnDescriptor.setCompressionType(Compression.Algorithm.SNAPPY);
			hTableDescriptor.addFamily(hColumnDescriptor);
		}
		admin.createTable(hTableDescriptor);
	}

}
