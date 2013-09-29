package org.archivarium.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

public class AlternateRowCellRenderer extends DefaultTableCellRenderer {
	private static final Color DEFAULT_BG = Color.white;
	private static final Color DEFAULT_BG_ALTERNATE = new Color(230, 230, 230);
	private static final Color DEFAULT_BG_SELECTED = new Color(170, 205, 135);
	private static final Color DEFAULT_FG = Color.black;
	private static final Color DEFAULT_FG_SELECTED = Color.white;

	private Color bgDefault, bgAlternate, bgSelected, fgDefault, fgSelected;

	public AlternateRowCellRenderer() {
		this(DEFAULT_BG, DEFAULT_BG_ALTERNATE, DEFAULT_BG_SELECTED, DEFAULT_FG,
				DEFAULT_FG_SELECTED);
	}

	public AlternateRowCellRenderer(Color bgDefault, Color bgAlternate,
			Color bgSelected, Color fgDefault, Color fgSelected) {
		this.bgDefault = bgDefault;
		this.bgAlternate = bgAlternate;
		this.bgSelected = bgSelected;
		this.fgDefault = fgDefault;
		this.fgSelected = fgSelected;
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
			boolean isSortedColumn = false;
			RowSorter<? extends TableModel> sorter = table.getRowSorter();
			if (sorter != null) {
				List<? extends SortKey> keys = sorter.getSortKeys();
				if (keys.size() > 0 && column == keys.get(0).getColumn()) {
					isSortedColumn = true;
				}
			}

			if (isSortedColumn || row % 2 == 1) {
				c.setBackground(bgAlternate);
			} else {
				c.setBackground(bgDefault);
			}
		}

		// Foreground
		c.setForeground(isSelected ? fgSelected : fgDefault);

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
