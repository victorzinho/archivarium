package org.archivarium.ui.listeners;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import geomatico.events.EventBus;
import geomatico.events.ExceptionEvent;
import geomatico.events.ExceptionEvent.Severity;
import geomatico.events.ExceptionEventHandler;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

import org.archivarium.ui.UIFactory;
import org.archivarium.ui.data.DataHandler;
import org.archivarium.ui.data.DataHandlerException;
import org.archivarium.ui.data.DataSource;
import org.archivarium.ui.data.Row;
import org.archivarium.ui.panels.ArchivariumMainPanel;
import org.archivarium.ui.panels.LocalDataPanel;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class ShowPopupListenerTest {
	private EventBus bus;
	private ExceptionEventHandler exceptionHandler;
	private UIFactory factory;

	private DataHandler<Row> handler;
	private MouseEvent e;
	private JTable table;

	@Before
	public void setUp() {
		bus = EventBus.getInstance();
		bus.removeAllHandlers();
		exceptionHandler = mock(ExceptionEventHandler.class);
		bus.addHandler(ExceptionEvent.class, exceptionHandler);
		factory = mock(UIFactory.class);
	}

	@Test
	public void testNoSelection() throws Exception {
		JTable table = mock(JTable.class);
		when(table.rowAtPoint(any(Point.class))).thenReturn(-1);
		ArchivariumMainPanel<Row> parent = mockMainPanel();

		ShowPopupListener<Row> listener = new ShowPopupListener<Row>(table,
				parent, factory, bus);
		listener.mouseClicked(mock(MouseEvent.class));

		verify(table).clearSelection();
		verify(handler, never()).open(anyInt());
	}

	@Test
	public void testOpenException() throws Exception {
		ArchivariumMainPanel<Row> parent = mockMainPanel(true);
		doThrow(DataHandlerException.class).when(handler).open(anyInt());

		ShowPopupListener<Row> listener = new ShowPopupListener<Row>(table,
				parent, factory, bus);
		listener.mouseClicked(e);

		verify(table).setRowSelectionInterval(1, 1);
		verify(exceptionHandler).exception(any(Severity.class), anyString(),
				any(Throwable.class));
	}

	@Test
	public void testNotOpenable() throws Exception {
		ArchivariumMainPanel<Row> parent = mockMainPanel(false);
		ShowPopupListener<Row> listener = new ShowPopupListener<Row>(table,
				parent, factory, bus);
		listener.mouseClicked(e);

		verify(table).setRowSelectionInterval(1, 1);
		verify(exceptionHandler, never()).exception(any(Severity.class),
				anyString(), any(Throwable.class));
		verify(handler, never()).open(anyInt());
	}

	@Test
	public void testOpen() throws Exception {
		ArchivariumMainPanel<Row> parent = mockMainPanel(true);
		ShowPopupListener<Row> listener = new ShowPopupListener<Row>(table,
				parent, factory, bus);
		listener.mouseClicked(e);
		verify(table).setRowSelectionInterval(1, 1);
		verify(exceptionHandler, never()).exception(any(Severity.class),
				anyString(), any(Throwable.class));
		verify(handler).open(anyInt());
	}

	private ArchivariumMainPanel<Row> mockMainPanel() {
		handler = mock(DataHandler.class);
		ArchivariumMainPanel<Row> panel = mock(ArchivariumMainPanel.class);
		when(panel.getDataHandler()).thenReturn(handler);
		return panel;
	}

	private ArchivariumMainPanel<Row> mockMainPanel(boolean rowOpenable)
			throws Exception {
		table = mock(JTable.class);
		when(table.rowAtPoint(any(Point.class))).thenReturn(1);
		when(table.getRowCount()).thenReturn(5);
		when(table.getValueAt(anyInt(), anyInt())).thenReturn(0);

		ArchivariumMainPanel<Row> parent = mockMainPanel();

		// Mock row
		DataSource<Row> source = mock(DataSource.class);
		Row row = mock(Row.class);
		when(row.isOpenable()).thenReturn(rowOpenable);
		when(source.getRowById(anyInt())).thenReturn(row);
		LocalDataPanel<Row> local = mock(LocalDataPanel.class);
		when(local.getDataSource()).thenReturn(source);
		when(parent.getLocalDataPanel()).thenReturn(local);

		// Mock mouse event
		e = mock(MouseEvent.class);
		when(e.getClickCount()).thenReturn(2);
		when(e.getButton()).thenReturn(MouseEvent.BUTTON1);

		return parent;
	}
}
