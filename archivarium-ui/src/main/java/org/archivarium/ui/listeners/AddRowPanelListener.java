package org.archivarium.ui.listeners;

import geomatico.events.EventBus;
import geomatico.events.ExceptionEvent;

import org.archivarium.ui.data.DataHandler;
import org.archivarium.ui.data.DataHandlerException;
import org.archivarium.ui.data.Row;
import org.archivarium.ui.data.RowEditionPanel;
import org.archivarium.ui.panels.AcceptCancelPanel;
import org.archivarium.ui.panels.ArchivariumMainPanel;
import org.archivarium.ui.panels.ArchivariumMainPanel.Tab;

public class AddRowPanelListener<T extends Row> implements
		AcceptCancelPanel.Listener {

	private RowEditionPanel<T> editPanel;
	private ArchivariumMainPanel<T> mainPanel;
	private DataHandler<T> handler;
	private EventBus eventBus;

	public AddRowPanelListener(ArchivariumMainPanel<T> mainPanel,
			EventBus eventBus) {
		this.mainPanel = mainPanel;
		this.handler = mainPanel.getDataHandler();
		this.editPanel = handler.getRowEditionPanel();
		this.eventBus = eventBus;
	}

	@Override
	public void accept() {
		try {
			handler.add(editPanel.getRow());
			editPanel.setRow(null);
			mainPanel.selectTab(Tab.LOCAL);
		} catch (DataHandlerException exception) {
			eventBus.fireEvent(new ExceptionEvent("cannot_update_score",
					exception));
		}
	}

	@Override
	public void cancel() {
		editPanel.setRow(null);
	}
}
