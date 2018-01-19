package com.fxf.extract.hbase;

import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HBaseUtil {

	public static void saveBatchData(TableName tableName, List<Map<String, String>> columns, String rowpattern) {
		Connection connection = null;
		Admin admin = null;
		try {

			if (CollectionUtils.isEmpty(columns)) return;

			connection = HBaseConnectionUtil.getConnection();
			admin = connection.getAdmin();
			if (!admin.tableExists(tableName)) {
				throw new RuntimeException(MessageFormat.format("表{0}不存在", tableName.getNameAsString()));
			}
			List<Map<String, String[]>> query = query(connection, admin, tableName, rowpattern);
			if(contains(query,rowpattern)){
				return;
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

	/**
	 * 物理行返回
	 *
	 * @param connection
	 * @param admin
	 * @param tableName
	 * @param rowPattern
	 * @throws IOException
	 */
	public static List<Map<String, String[]>> query(Connection connection, Admin admin, TableName tableName, String rowPattern) throws IOException {

		boolean b = admin.tableExists(tableName);
		if (!b) {
			throw new RuntimeException(MessageFormat.format("表{0}不存在", tableName.getNameAsString()));
		}
		Table table = connection.getTable(tableName);
		RowFilter rowFilter = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(rowPattern));
		Scan scan = new Scan(HConstants.EMPTY_START_ROW, rowFilter);
		ResultScanner scanner = table.getScanner(scan);
		List<Map<String, String[]>> data = new ArrayList<>();
		for (Result result : scanner) {
			Map<String, String[]> map = new HashMap<>();
			Cell[] cells = result.rawCells();
			for (Cell cell : cells) {
				String row = Bytes.toString(CellUtil.cloneRow(cell));
				map.put(row, new String[]{
						Bytes.toString(CellUtil.cloneFamily(cell)),
						Bytes.toString(CellUtil.cloneQualifier(cell)),
						Bytes.toString(CellUtil.cloneValue(cell))
				});
			}
			data.add(map);
		}
		return data;
	}

	public static boolean contains(List<Map<String, String[]>> datas, String rowPattern){
		if (CollectionUtils.isEmpty(datas)) {
			return false;
		}
		for (Map<String, String[]> data : datas) {
			Set<Map.Entry<String, String[]>> entries = data.entrySet();
			for (Map.Entry<String, String[]> entry : entries){
				if(match(entry.getKey(),rowPattern)){
					return true;
				}
			}
		}
		return false;
	}

	public static boolean contains(List<Map<String, String[]>> datas, String rowPattern, String family, String qualifier) throws IOException {
		if (CollectionUtils.isEmpty(datas)) {
			return false;
		}
		for (Map<String, String[]> data : datas) {
			Set<Map.Entry<String, String[]>> entries = data.entrySet();
			for (Map.Entry<String, String[]> entry : entries){
				if(!match(entry.getKey(),rowPattern)){
					return false;
				}
				String[] value = entry.getValue();
				boolean containFamily = false;
				boolean containQualifier = false;
				for (String str : value) {
					if (str.equals(family)) {
						containFamily = true;
					}
					if (str.equals(qualifier)) {
						containQualifier = true;
					}
				}
				if (containFamily && containQualifier) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean match(String str,String pattern){
		Pattern compile = Pattern.compile(pattern);
		Matcher matcher = compile.matcher(str);
		return matcher.find();
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

	public static void main(String[] args) {
		Connection connection = null;
		Admin admin = null;
		try {
			TableName tableName = TableName.valueOf("web_crawler_content");
			connection = HBaseConnectionUtil.getConnection();
			admin = connection.getAdmin();

			List<Map<String, String[]>> data = query(connection, admin, tableName, "_AFB268F6E17000C00BDB8AF4D43C2085$");
			boolean f1 = contains(data, "_AFB268F6E17000C00BDB8AF4D43C2085$");
			System.out.println("------------->>>>>>>>>"+f1);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			HBaseConnectionUtil.close(connection, admin);
		}
	}
}
