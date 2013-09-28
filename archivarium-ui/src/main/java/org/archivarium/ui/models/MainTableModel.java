package org.archivarium.ui.models;

import javax.swing.table.AbstractTableModel;

import org.archivarium.ui.data.DataHandlerException;
import org.archivarium.ui.data.DataSource;
import org.archivarium.ui.data.Row;
import org.archivarium.ui.events.DataChangeEventHandler;

public class MainTableModel extends AbstractTableModel implements
		DataChangeEventHandler {
	public static final int COLUMN_ID = 0;

	private DataSource<?> source;
	private Row[] rows;

	public MainTableModel(DataSource<?> source) throws DataHandlerException {
		super();
		this.source = source;
		this.rows = source.getRows();
	}

	@Override
	public int getRowCount() {
		return rows.length;
	}

	@Override
	public int getColumnCount() {
		// + 1 for ID
		return source.getColumnCount() + 1;
	}

	@Override
	public String getColumnName(int column) {
		// - 1 for ID
		return column == COLUMN_ID ? "id" : source.getColumnName(column - 1);
	}

	@Override
	public Object getValueAt(int rowIndex, int column) {
		Row row = rows[rowIndex];
		// - 1 for ID
		return column == COLUMN_ID ? row.getId() : row.getData(column - 1);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (getRowCount() > 0) {
			Object value = getValueAt(0, columnIndex);
			if (value != null) {
				return value.getClass();
			}
		}

		return super.getColumnClass(columnIndex);
	}

	@Override
	public void dataChanged(DataSource<?> source) {
		if (this.source == source) {
			rows = source.getRows();
			fireTableDataChanged();
		}
	}
}