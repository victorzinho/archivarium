package org.archivarium.ui.listeners;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import geomatico.events.EventBus;
import geomatico.events.ExceptionEvent;
import geomatico.events.ExceptionEvent.Severity;
import geomatico.events.ExceptionEventHandler;

import org.archivarium.ui.data.DataHandler;
import org.archivarium.ui.data.DataHandlerException;
import org.archivarium.ui.data.Row;
import org.archivarium.ui.data.RowEditionPanel;
import org.archivarium.ui.panels.ArchivariumMainPanel;
import org.junit.Before;
import org.junit.Test;

public class UpdateRowPanelListenerTest {

	private EventBus bus;
	private ExceptionEventHandler exceptionHandler;

	private DataHandler<Row> handler;
	private RowEditionPanel<Row> editPanel;

	@Before
	public void setUp() {
		bus = EventBus.getInstance();
		bus.removeAllHandlers();
		exceptionHandler = mock(ExceptionEventHandler.class);
		bus.addHandler(ExceptionEvent.class, exceptionHandler);
	}

	@Test
	public void updateException() throws Exception {
		ArchivariumMainPanel<Row> mainPanel = mockMainPanel();
		doThrow(DataHandlerException.class).when(handler)
				.update(any(Row.class));

		UpdateRowPanelListener<Row> listener = new UpdateRowPanelListener<Row>(
				mainPanel, bus);
		listener.accept();

		verify(mainPanel, never()).closeUpdateTab(same(editPanel));
		verify(exceptionHandler).exception(any(Severity.class), anyString(),
				any(Throwable.class));
	}

	@Test
	public void update() throws Exception {
		ArchivariumMainPanel<Row> mainPanel = mockMainPanel();

		UpdateRowPanelListener<Row> listener = new UpdateRowPanelListener<Row>(
				mainPanel, bus);
		listener.accept();

		verify(mainPanel).closeUpdateTab(same(editPanel));
		verify(exceptionHandler, never()).exception(any(Severity.class),
				anyString(), any(Throwable.class));
		verify(handler).update(any(Row.class));
	}

	@Test
	public void cancel() throws Exception {
		ArchivariumMainPanel<Row> mainPanel = mockMainPanel();
		UpdateRowPanelListener<Row> listener = new UpdateRowPanelListener<Row>(
				mainPanel, bus);
		listener.cancel();
		verify(mainPanel).closeUpdateTab(same(editPanel));
	}

	@SuppressWarnings("unchecked")
	private ArchivariumMainPanel<Row> mockMainPanel() {
		editPanel = mock(RowEditionPanel.class);

		handler = mock(DataHandler.class);
		when(handler.getUpdatePanel()).thenReturn(editPanel);

		ArchivariumMainPanel<Row> panel = mock(ArchivariumMainPanel.class);
		when(panel.getDataHandler()).thenReturn(handler);

		return panel;
	}
}
