package org.archivarium.ui;

import javax.swing.Icon;
import javax.swing.table.TableCellRenderer;

public interface UIFactory {
	Icon getIcon(String resource);

	TableCellRenderer getTableCellRenderer();
}
