package org.archivarium.ui.listeners;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
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
import org.archivarium.ui.panels.ArchivariumMainPanel.Tab;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class AddRowPanelListenerTest {
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
	public void addException() throws Exception {
		ArchivariumMainPanel<Row> mainPanel = mockMainPanel();
		doThrow(DataHandlerException.class).when(handler).add(any(Row.class));

		AddRowPanelListener<Row> listener = new AddRowPanelListener<Row>(
				mainPanel, bus);
		listener.accept();

		verify(editPanel, never()).setRow(any(Row.class));
		verify(mainPanel, never()).selectTab(any(Tab.class));
		verify(exceptionHandler).exception(any(Severity.class), anyString(),
				any(Throwable.class));
	}

	@Test
	public void add() throws Exception {
		ArchivariumMainPanel<Row> mainPanel = mockMainPanel();

		AddRowPanelListener<Row> listener = new AddRowPanelListener<Row>(
				mainPanel, bus);
		listener.accept();

		ArgumentCaptor<Row> r1 = ArgumentCaptor.forClass(Row.class);
		ArgumentCaptor<Row> r2 = ArgumentCaptor.forClass(Row.class);
		verify(editPanel).setRow(r1.capture());
		verify(mainPanel).selectTab(same(Tab.LOCAL));
		verify(handler).add(r2.capture());
		assertEquals(r1.getValue(), r2.getValue());
	}

	@Test
	public void cancel() throws Exception {
		ArchivariumMainPanel<Row> mainPanel = mockMainPanel();
		AddRowPanelListener<Row> listener = new AddRowPanelListener<Row>(
				mainPanel, bus);
		listener.cancel();
		verify(editPanel).setRow(isNull(Row.class));
	}

	@SuppressWarnings("unchecked")
	private ArchivariumMainPanel<Row> mockMainPanel() {
		editPanel = mock(RowEditionPanel.class);

		handler = mock(DataHandler.class);
		when(handler.getRowEditionPanel()).thenReturn(editPanel);

		ArchivariumMainPanel<Row> panel = mock(ArchivariumMainPanel.class);
		when(panel.getDataHandler()).thenReturn(handler);

		return panel;
	}
}
