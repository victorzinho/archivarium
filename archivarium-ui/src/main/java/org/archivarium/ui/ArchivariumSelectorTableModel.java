package org.archivarium.ui;

import javax.swing.table.AbstractTableModel;

class ArchivariumSelectorTableModel extends AbstractTableModel {

	private String name;
	private String[] values;

	public ArchivariumSelectorTableModel(String name, String[] values) {
		super();
		this.name = name;
		this.values = values;
	}

	@Override
	public int getRowCount() {
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
		if (rowIndex == 0) {
			return "All";
		} else {
			return values[rowIndex - 1];
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void setValues(String[] values) {
		this.values = values;
	}
}
