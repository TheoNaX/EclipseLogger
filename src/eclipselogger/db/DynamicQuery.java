package eclipselogger.db;

import java.util.ArrayList;
import java.util.List;

public class DynamicQuery {
	private String tableName;
	private String tableJoinColumn;
	private String joinedTableName;
	private String joinedTableJoinColumn;
	private final List<WhereParam> whereParams = new ArrayList<WhereParam>();
	
	private final List<String> columnsToSelect = new ArrayList<String>();
	private List<String> columnsFromJoinedTable;
	
	public DynamicQuery(final String tableName, final DynamicQuery joined) {
		this.tableName = tableName;
		this.joinedTableName = joined.getTableName();
		this.columnsFromJoinedTable = joined.getListOfSelectParams();
	}
	
	public DynamicQuery(final String tableName) 
	{
		this.tableName = tableName;
	}
	
	public void addColumnToSelect(final String columnName) {
		this.columnsFromJoinedTable.add(columnName);
	}
	
	public void setTableName(final String tableName) {
		this.tableName = tableName;
	}
	
	public void setJoinColumn(final String joinColumn) {
		this.tableJoinColumn = joinColumn;
	}
	
	public void setJoinColumnForJoinedTable(final String column) {
		this.joinedTableJoinColumn = column;
	}
	
	public List<String> getListOfSelectParams() {
		return this.columnsToSelect;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	public String getJoinColumn() {
		return this.tableJoinColumn;
	}
	
	public String buildQuery() {
		final String sqlInsert;
		if (this.tableJoinColumn != null && this.joinedTableJoinColumn != null) {
			return buildJoinedTablesQuery();
		} else {
			return buildSingleTableQuery();
		}
	}
	
	private String buildJoinedTablesQuery() {
		final StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		for (int i=0; i<this.columnsToSelect.size(); i++) {
			final String column = this.columnsToSelect.get(i);
			sb.append("a." + column);
			sb.append(", ");
		}
		
		for (int i=0; i<this.columnsFromJoinedTable.size(); i++) {
			final String column = this.columnsFromJoinedTable.get(i);
			sb.append("b." + column);
			sb.append(", ");
		}
		if (sb.toString().length() > 7) {
			sb.deleteCharAt(sb.lastIndexOf(", "));
		}
		sb.append(" FROM " + this.tableName + " a");
		sb.append(" INNER JOIN " + this.joinedTableName);
		sb.append(" ON a." + this.tableJoinColumn + " = b." + this.joinedTableJoinColumn);
		
		if (this.whereParams.size() > 0) {
			applyWhereParams(sb);
		}
		sb.append(";");
		
		System.out.println(">>>>>>>>> QUERY: " + sb);
		return sb.toString();
	}
	
	private String buildSingleTableQuery() {
		final StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		for (int i=0; i<this.columnsToSelect.size(); i++) {
			final String column = this.columnsToSelect.get(i);
			sb.append(column);
			sb.append(", ");
		}
		if (sb.toString().length() > 7) {
			sb.deleteCharAt(sb.lastIndexOf(", "));
		}
		sb.append(" FROM ");
		sb.append(this.tableName);
		
		// apply where condition
		if (this.whereParams.size() > 0) {
			applyWhereParams(sb);
		}
		
		sb.append(";");
		
		System.out.println(">>>>>>>>> QUERY: " + sb);
		return sb.toString();
		
	}
	
	private void applyWhereParams(final StringBuilder sb) {
		if (this.whereParams.size() == 1) {
			final String column = this.whereParams.get(0).getColumnName();
			final String condition = this.whereParams.get(0).getCondition();
			sb.append(" WHERE " + column + condition + " ?");
		} else {
			// TODO if more where conditions
		}
	}
	
	public void addWhereParam(final WhereParam wherePar) {
		this.whereParams.add(wherePar);
	}
	
	public static class WhereParam {
		private final String columnName;
		private final String condition;
		
		public static final String CONDITION_EQUAL = " = ";
		public static final String CONDITION_LARGER = " > ";
		public static final String CONDITION_SMALLER = " < ";
		
		public WhereParam(final String column, final String condition) {
			this.columnName = column;
			this.condition = condition;
		}
		
		public WhereParam(final String column) {
			this.columnName = column;
			this.condition = CONDITION_EQUAL;
		}
		
		public String getColumnName() {
			return this.columnName;
		}
		
		public String getCondition() {
			return this.condition;
		}
	}
	
}
