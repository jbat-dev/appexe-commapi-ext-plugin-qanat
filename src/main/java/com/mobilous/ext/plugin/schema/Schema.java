package com.mobilous.ext.plugin.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mobilous.ext.plugin.constant.DatasetKey;
import com.mobilous.ext.plugin.util.HeterogeneousMap;

/**
 * Define your database schema here.
 *
 * @author yanto
 */
public class Schema {

	public enum Table {

		// TODO Define the table names and the corresponding schema objects here.
		TABLE_01("table_name", tableSchema);

		private String tablename;
		private HeterogeneousMap schema;

		Table(String tablename, HeterogeneousMap schema) {
			this.tablename = tablename;
			this.schema = schema;
		}

		public String getTablename() {
			return tablename;
		}

		public HeterogeneousMap getSchema() {
			return schema;
		}

		public static HeterogeneousMap getTablenames() {
			HeterogeneousMap map = new HeterogeneousMap();

			List<String> relations = new ArrayList<String>();
			for (Table t : Table.values())
				relations.add(t.getTablename());

			map.put(DatasetKey.SCHEMA.getKey(), relations, List.class);

			return map;
		}

		public static HeterogeneousMap retrieveSchema(String tablename) {
			if (!StringUtils.isBlank(tablename)) {
				for (Table t : Table.values()) {
					if (tablename.equalsIgnoreCase(t.tablename)) {
						return t.getSchema();
					}
				}
			}
			return null;
		}
	}
	
	public enum Field {

		// TODO Define the field names here. You may divide them into separate enums too.
		FIELD_01("field_01"),
		FIELD_02("field_02"),
		FIELD_03("field_03"),
		FIELD_04("field_04"),
		FIELD_05("field_05");
		
		private String fieldname;
		
		Field(String fieldname) {
			this.fieldname = fieldname;
		}
		
		public String getValue() {
			return fieldname;
		}
	}


	// TODO Define the schema object here. One schema object per table.
	private static final HeterogeneousMap tableSchema;
	static {
		List<String> booleanFields = Arrays.asList(Field.FIELD_01.getValue());
		List<String> dateFields = Arrays.asList(Field.FIELD_02.getValue());
		List<String> integerFields = Arrays.asList(Field.FIELD_03.getValue());
		List<String> realFields = Arrays.asList(Field.FIELD_04.getValue());
		List<String> textFields = Arrays.asList(Field.FIELD_05.getValue());

		HeterogeneousMap schema = new HeterogeneousMap();
		schema.put(DataType.BOOLEAN.getValue(), booleanFields, List.class);
		schema.put(DataType.DATE.getValue(), dateFields, List.class);
		schema.put(DataType.INTEGER.getValue(), integerFields, List.class);
		schema.put(DataType.REAL.getValue(), realFields, List.class);
		schema.put(DataType.TEXT.getValue(), textFields, List.class);

		tableSchema = schema;
	};
}
