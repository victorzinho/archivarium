package org.archivarium.ui;

import geomatico.events.EventBus;
import geomatico.events.ExceptionEvent;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.archivarium.ui.data.TableDataException;
import org.archivarium.ui.data.DataSource;
import org.archivarium.ui.data.Row;

class ArchivariumScoreTableModel extends AbstractTableModel {
	private static final Logger logger = Logger
			.getLogger(ArchivariumScoreTableModel.class);

	static final int COLUMN_ID = 0;

	private DataSource source;
	private String[] criteria;

	public ArchivariumScoreTableModel(DataSource source, String[] criteria) {
		super();
		this.source = source;
		this.criteria = criteria;
	}

	@Override
	public int getRowCount() {
		try {
			return getRows().length;
		} catch (TableDataException e) {
			logger.error("Cannot obtain data rows", e);
			EventBus.getInstance().fireEvent(
					new ExceptionEvent(e.getMessage(), e));
			return 0;
		}
	}

	@Override
	public int getColumnCount() {
		// + 1 for hidden ID
		return source.getColumnCount() + 1;
	}

	@Override
	public String getColumnName(int column) {
		// - 1 for hidden ID
		return (column == COLUMN_ID) ? "id" : source.getColumnName(column - 1);
	}

	private Row[] getRows() throws TableDataException {
		return source.getRows(criteria);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
			Row row = getRows()[rowIndex];
			if (columnIndex == COLUMN_ID) {
				return row.getId();
			} else {
				return row.getData(columnIndex - 1);
			}
		} catch (TableDataException exception) {
			EventBus.getInstance().fireEvent(
					new ExceptionEvent(exception.getMessage(), exception));
			return null;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Object value = getValueAt(0, columnIndex);
		return value != null ? value.getClass() : super
				.getColumnClass(columnIndex);
	}

	public void setCriteria(String[] criteria) {
		this.criteria = criteria;
		fireTableDataChanged();
	}
}