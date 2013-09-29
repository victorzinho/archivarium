package org.archivarium.ui.models;

import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import org.archivarium.ui.data.DataHandlerException;
import org.archivarium.ui.data.DataSource;

public class SelectorTableModel extends AbstractTableModel {
	private String name;
	private String[] values;
	private ResourceBundle messages;

	public SelectorTableModel(DataSource<?> source, int column,
			ResourceBundle messages) throws DataHandlerException {
		super();

		this.name = source.getColumnName(column);
		this.values = source.getUniqueValues(column);
		this.messages = messages;
	}

	@Override
	public int getRowCount() {
		// + 1 for "All" category
		return values.length + 1;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int column) {
		return name;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// - 1 for "All" category
		return rowIndex == 0 ? messages.getString("all") : values[rowIndex - 1];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
		fireTableDataChanged();
	}
}
