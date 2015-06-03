package jp.co.jbat.qanat.rest.test;

import java.util.List;

import org.json.simple.JSONArray;
import org.junit.Test;

import com.mobilous.ext.plugin.constant.DatasetKey;
import com.mobilous.ext.plugin.impl.PluginServiceImpl;
import com.mobilous.ext.plugin.schema.DataType;
import com.mobilous.ext.plugin.util.HeterogeneousMap;

public class PluginTest {

	// public static void main(String args[]) {
	@Test
	public void Test() {
		try {
			PluginServiceImpl service = new PluginServiceImpl();

			HeterogeneousMap dataset = new HeterogeneousMap();

			HeterogeneousMap getService;
			HeterogeneousMap getTableName;
			HeterogeneousMap getRecordNum;
			HeterogeneousMap getCoulumn;
			HeterogeneousMap getRead;

			System.out.println("Try : getServiceName()");
			getService = service.getServiceName();
			if (!getService.get("use_api", Boolean.class)) {
				System.out.println("getService : invalid");
			}
			System.out.println("Done : getServiceName()");
			System.out.println("ServiceName : "
					+ getService.get(DatasetKey.SERVICENAME.getKey()));

			// set table
			System.out.println("Try : getSchema()");
			getTableName = service.getSchema(dataset);
			if (getTableName.get("auth_status", String.class).equals("invalid")) {
				System.out.println("getTableName : invalid");
			} else {
				System.out.println("Done : getSchema()");

				@SuppressWarnings("unchecked")
				List<String> tables = getTableName.get(
						DatasetKey.SCHEMA.getKey(), List.class);
				System.out.println("tables : " + tables);

				// using first tables
				dataset.put(DatasetKey.TABLE.getKey(), tables.get(0));
			}
			
			// set field
			System.out.println("Try : getSchema() and set fieldName");
			getCoulumn = service.getSchema(dataset);
			if (getCoulumn.get("auth_status", String.class).equals("invalid")) {
				System.out.println("getCoulumn : invalid");
			} else {
				System.out.println("Done : getSchema() and set fieldName");
				System.out.println("FieldName");
				@SuppressWarnings("unchecked")
				List<String> textfields = getCoulumn.get(
						DataType.TEXT.getValue(), List.class);
				if (textfields != null) {
					for (String str : textfields) {
						System.out.println("TEXT_Field : " + str);
					}
				}
				@SuppressWarnings("unchecked")
				List<String> intfields = getCoulumn.get(
						DataType.INTEGER.getValue(), List.class);
				if (intfields != null) {
					for (String str : intfields) {
						System.out.println("INTEGER_Field : " + str);
					}
				}
				@SuppressWarnings("unchecked")
				List<String> realfields = getCoulumn.get(
						DataType.REAL.getValue(), List.class);
				if (realfields != null) {
					for (String str : realfields) {
						System.out.println("REAL_Field : " + str);
					}
				}
				@SuppressWarnings("unchecked")
				List<String> datefields = getCoulumn.get(
						DataType.DATE.getValue(), List.class);
				if (datefields != null) {
					for (String str : datefields) {
						System.out.println("DATE_Field : " + str);
					}
				}
				@SuppressWarnings("unchecked")
				List<String> boolfields = getCoulumn.get(
						DataType.BOOLEAN.getValue(), List.class);
				if (boolfields != null) {
					for (String str : boolfields) {
						System.out.println("BOOL_Field : " + str);
					}
				}
			}
			// getNumrecord
			dataset.put(DatasetKey.WHERE.getKey(), null);
			System.out.println("Try : numrecord");
			getRecordNum = service.numrecord(dataset);
			if (getRecordNum.get(DatasetKey.RETURN_STATUS.getKey(), String.class).equals("invalid")) {
				System.out.println("getRecordNum : invalid");
			} else {
				System.out.println("Done : numrecord");
				System.out.println("numrecord : "
						+ getRecordNum.get(DatasetKey.NUMBEROFRECORD.getKey(),
								String.class));
			}
			// get READ
			dataset.put(DatasetKey.WHERE.getKey(), null);
			System.out.println("Try : read");
			getRead = service.read(dataset);
			if (getRead.get(DatasetKey.RETURN_STATUS.getKey(), String.class).equals("invalid")) {
				System.out.println("getRead : invalid");
			} else {
				System.out.println("Done : read");
				System.out.println(getRead.get(DatasetKey.DATA.getKey(),
						JSONArray.class));
			}
		} catch (Exception e) {
			System.err.println("Error Occurred");
			e.printStackTrace();
		}
	}

}