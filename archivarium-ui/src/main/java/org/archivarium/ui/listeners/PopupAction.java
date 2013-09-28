package org.archivarium.ui.listeners;

import geomatico.events.EventBus;
import geomatico.events.ExceptionEvent;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import org.archivarium.ui.data.DataHandlerException;
import org.archivarium.ui.data.Row;
import org.archivarium.ui.panels.ArchivariumMainPanel;

public class PopupAction<T extends Row> extends AbstractAction {
	public static String UPDATE = "update";
	public static String DELETE = "delete";

	private ArchivariumMainPanel<T> mainPanel;
	private EventBus eventBus;

	public PopupAction(String name, Icon icon,
			ArchivariumMainPanel<T> mainPanel, EventBus eventBus) {
		super(name, icon);
		this.mainPanel = mainPanel;
		this.eventBus = eventBus;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action.equals(UPDATE)) {
			mainPanel.selectUpdateTab();
		} else if (action.equals(DELETE)) {
			try {
				mainPanel.getDataHandler().delete(mainPanel.getSelectedId());
			} catch (DataHandlerException exception) {
				eventBus.fireEvent(new ExceptionEvent("cannot_remove_score",
						exception));
			}
		}
	}
}
