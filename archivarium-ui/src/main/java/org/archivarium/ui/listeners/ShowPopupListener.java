package org.archivarium.ui.listeners;

import geomatico.events.EventBus;
import geomatico.events.ExceptionEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.archivarium.ui.UIFactory;
import org.archivarium.ui.data.DataHandlerException;
import org.archivarium.ui.data.DataSource;
import org.archivarium.ui.data.Row;
import org.archivarium.ui.models.MainTableModel;
import org.archivarium.ui.panels.ArchivariumMainPanel;

public class ShowPopupListener<T extends Row> extends MouseAdapter {
	private JTable table;
	private ArchivariumMainPanel<T> parent;
	private UIFactory factory;
	private EventBus eventBus;

	public ShowPopupListener(JTable table, ArchivariumMainPanel<T> parent,
			UIFactory factory, EventBus eventBus) {
		this.table = table;
		this.parent = parent;
		this.factory = factory;
		this.eventBus = eventBus;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int rowIndex = table.rowAtPoint(e.getPoint());
		if (rowIndex >= 0 && rowIndex < table.getRowCount()) {
			table.setRowSelectionInterval(rowIndex, rowIndex);
		} else {
			table.clearSelection();
			return;
		}

		if (e.getButton() == MouseEvent.BUTTON3) {
			JPopupMenu popup = new JPopupMenu();
			JMenuItem delete = new JMenuItem(new PopupAction<T>(
					PopupAction.DELETE, factory.getIcon("delete.png"), parent,
					eventBus));
			JMenuItem update = new JMenuItem(new PopupAction<T>(
					PopupAction.UPDATE, factory.getIcon("update.png"), parent,
					eventBus));
			popup.add(delete);
			popup.add(update);
			popup.show(e.getComponent(), e.getX(), e.getY());
		} else if (e.getClickCount() == 2
				&& e.getButton() == MouseEvent.BUTTON1) {
			int id = (Integer) table.getValueAt(rowIndex,
					MainTableModel.COLUMN_ID);
			DataSource<T> source = parent.getLocalDataPanel().getDataSource();

			try {
				Row row = source.getRowById(id);
				if (row != null && row.isOpenable()) {
					parent.getDataHandler().open(row.getId());
				}
			} catch (DataHandlerException exception) {
				eventBus.fireEvent(new ExceptionEvent(exception.getMessage(),
						exception));
			}
		}
	}
}
