package org.archivarium.ui;

import geomatico.events.EventBus;
import geomatico.events.ExceptionEvent;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.archivarium.ui.data.TableDataException;
import org.archivarium.ui.data.DataHandler;
import org.archivarium.ui.data.DataSource;

class ArchivariumPopupMenu extends JPopupMenu {
	private static final URL DELETE_ICON = ArchivariumPopupMenu.class
			.getResource("delete.png");
	private static final URL UPDATE_ICON = ArchivariumPopupMenu.class
			.getResource("update.png");

	private JTable table;

	public ArchivariumPopupMenu(JTable t, final DataHandler handler,
			final DataSource provider) {
		super();

		this.table = t;

		JMenuItem delete = new JMenuItem(new AbstractAction("Delete",
				new ImageIcon(DELETE_ICON)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					handler.remove(getSelectedId());
				} catch (TableDataException exception) {
					EventBus.getInstance().fireEvent(
							new ExceptionEvent("Cannot remove score: "
									+ exception.getMessage(), exception));
				}
			}
		});

		JMenuItem update = new JMenuItem(new AbstractAction("Update",
				new ImageIcon(UPDATE_ICON)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					handler.update(getSelectedId());
				} catch (TableDataException exception) {
					EventBus.getInstance().fireEvent(
							new ExceptionEvent("Cannot update score: "
									+ exception.getMessage(), exception));
				}
			}
		});

		add(delete);
		add(update);
	}

	private int getSelectedId() {
		return (Integer) table.getValueAt(table.getSelectedRow(),
				ArchivariumScoreTableModel.COLUMN_ID);
	}
}
