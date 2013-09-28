package org.archivarium.ui.listeners;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import geomatico.events.Event;
import geomatico.events.EventBus;
import geomatico.events.ExceptionEvent;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import org.archivarium.ui.data.DataHandler;
import org.archivarium.ui.data.DataHandlerException;
import org.archivarium.ui.data.Row;
import org.archivarium.ui.panels.ArchivariumMainPanel;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class PopupActionTest {
	@Test
	public void update() {
		EventBus bus = mock(EventBus.class);
		ArchivariumMainPanel<Row> panel = mock(ArchivariumMainPanel.class);

		PopupAction<Row> action = new PopupAction<Row>("", mock(Icon.class),
				panel, bus);
		action.actionPerformed(new ActionEvent(new Object(), 0,
				PopupAction.UPDATE));

		verify(panel).selectUpdateTab();
	}

	@Test
	public void deleteException() throws Exception {
		EventBus bus = mock(EventBus.class);

		DataHandler<Row> handler = mock(DataHandler.class);
		doThrow(DataHandlerException.class).when(handler).delete(anyInt());

		ArchivariumMainPanel<Row> panel = mock(ArchivariumMainPanel.class);
		when(panel.getDataHandler()).thenReturn(handler);

		PopupAction<Row> action = new PopupAction<Row>("", mock(Icon.class),
				panel, bus);
		action.actionPerformed(new ActionEvent(new Object(), 0,
				PopupAction.DELETE));

		ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
		verify(bus).fireEvent(arg.capture());
		assertTrue(arg.getValue() instanceof ExceptionEvent);
	}

	@Test
	public void delete() throws DataHandlerException {
		EventBus bus = mock(EventBus.class);

		DataHandler<Row> handler = mock(DataHandler.class);
		ArchivariumMainPanel<Row> panel = mock(ArchivariumMainPanel.class);
		when(panel.getDataHandler()).thenReturn(handler);

		PopupAction<Row> action = new PopupAction<Row>("", mock(Icon.class),
				panel, bus);
		action.actionPerformed(new ActionEvent(new Object(), 0,
				PopupAction.DELETE));

		verify(handler).delete(anyInt());
		verify(bus, never()).fireEvent(any(Event.class));
	}
}
