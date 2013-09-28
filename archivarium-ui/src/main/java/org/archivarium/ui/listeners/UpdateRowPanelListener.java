package org.archivarium.ui.listeners;

import geomatico.events.EventBus;
import geomatico.events.ExceptionEvent;

import org.archivarium.ui.data.DataHandler;
import org.archivarium.ui.data.DataHandlerException;
import org.archivarium.ui.data.Row;
import org.archivarium.ui.data.RowEditionPanel;
import org.archivarium.ui.panels.AcceptCancelPanel;
import org.archivarium.ui.panels.ArchivariumMainPanel;

public class UpdateRowPanelListener<T extends Row> implements
		AcceptCancelPanel.Listener {

	private DataHandler<T> handler;
	private RowEditionPanel<T> editPanel;
	private ArchivariumMainPanel<T> mainPanel;
	private EventBus eventBus;

	public UpdateRowPanelListener(ArchivariumMainPanel<T> mainPanel,
			EventBus eventBus) {
		this.mainPanel = mainPanel;
		this.handler = mainPanel.getDataHandler();
		this.editPanel = handler.getRowEditionPanel();
		this.eventBus = eventBus;
	}

	@Override
	public void accept() {
		try {
			handler.update(editPanel.getRow());
			mainPanel.closeUpdateTab(editPanel);
		} catch (DataHandlerException exception) {
			eventBus.fireEvent(new ExceptionEvent("cannot_update_score",
					exception));
		}
	}

	@Override
	public void cancel() {
		mainPanel.closeUpdateTab(editPanel);
	}
}
