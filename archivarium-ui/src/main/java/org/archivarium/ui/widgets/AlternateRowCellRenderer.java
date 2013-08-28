package org.archivarium.ui.widgets;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

public class AlternateRowCellRenderer extends DefaultTableCellRenderer {
	private static final Color DEFAULT_BG = Color.white;
	private static final Color DEFAULT_BG_ALTERNATE = new Color(230, 230, 230);
	private static final Color DEFAULT_BG_SELECTED = new Color(170, 205, 135);

	private Color bgDefault, bgAlternate, bgSelected;

	public AlternateRowCellRenderer() {
		this(DEFAULT_BG, DEFAULT_BG_ALTERNATE, DEFAULT_BG_SELECTED);
	}

	public AlternateRowCellRenderer(Color bgDefault, Color bgAlternate,
			Color bgSelected) {
		this.bgDefault = bgDefault;
		this.bgAlternate = bgAlternate;
		this.bgSelected = bgSelected;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);

		// Background
		if (isSelected) {
			c.setBackground(bgSelected);
		} else {
			RowSorter<? extends TableModel> sorter = table.getRowSorter();
			boolean isSortedColumn = sorter != null
					&& sorter.getSortKeys().size() > 0
					&& column == sorter.getSortKeys().get(0).getColumn();

			if (isSortedColumn || row % 2 == 1) {
				c.setBackground(bgAlternate);
			} else {
				c.setBackground(bgDefault);
			}
			c.setForeground(Color.black);
		}

		// Foreground
		c.setForeground(isSelected ? Color.white : Color.black);

		return c;
	}

	@Override
	protected void setValue(Object value) {
		if (value instanceof Icon) {
			setIcon((Icon) value);
		} else {
			setIcon(null);
			super.setValue(value);
		}
	}
}
