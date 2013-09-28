package org.archivarium.ui.models;

import java.util.Arrays;

import javax.swing.JTable;

import org.archivarium.ui.data.DataSource;
import org.archivarium.ui.events.DataChangeEventHandler;

public class SelectorDataChangeHandler implements DataChangeEventHandler {
	private SelectorTableModel model;
	private JTable table;
	private int column;
	private DataSource<?> source;

	public SelectorDataChangeHandler(SelectorTableModel model,
			DataSource<?> source, JTable table, int column) {
		this.model = model;
		this.source = source;
		this.table = table;
		this.column = column;
	}

	@Override
	public void dataChanged(DataSource<?> source) {
		if (this.source != source) {
			return;
		}

		String[] currentValues = model.getValues();
		String[] newValues = source.getUniqueValues()[column];

		if (!Arrays.equals(currentValues, newValues)) {
			// Get selected item
			Object selected;
			int selectedRow = table.getSelectedRow();
			if (selectedRow > 0 && selectedRow < table.getModel().getRowCount()) {
				selected = table.getValueAt(selectedRow, 0);
			} else {
				selected = null;
			}

			// Set values
			model.setValues(newValues);

			// Set selected item (if exists)
			for (int i = 0; i < table.getRowCount(); i++) {
				Object value = table.getValueAt(i, 0);
				if (value.equals(selected)) {
					table.getSelectionModel().setSelectionInterval(i, i);
					return;
				}
			}

			table.getSelectionModel().setSelectionInterval(0, 0);
		}
	}
}
