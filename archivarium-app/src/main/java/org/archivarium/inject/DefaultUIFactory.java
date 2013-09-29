package org.archivarium.inject;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.TableCellRenderer;

import org.archivarium.ui.AlternateRowCellRenderer;
import org.archivarium.ui.UIFactory;

public class DefaultUIFactory implements UIFactory {

	@Override
	public Icon getIcon(String resource) {
		return new ImageIcon(getClass().getResource("/icons/" + resource));
	}

	@Override
	public TableCellRenderer getTableCellRenderer() {
		return new AlternateRowCellRenderer();
	}
}
