package org.archivarium.ui.listeners;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import geomatico.events.EventBus;

import javax.swing.event.ListSelectionEvent;

import org.archivarium.ui.events.CategoryChangeEvent;
import org.archivarium.ui.events.CategoryChangeEventHandler;
import org.archivarium.ui.panels.ArchivariumMainPanel;
import org.archivarium.ui.panels.Selector;
import org.junit.Before;
import org.junit.Test;

public class SelectorListenerTest {

	private static final String VALUE = "category";

	private EventBus bus;
	private CategoryChangeEventHandler handler;

	@Before
	public void setUp() {
		bus = EventBus.getInstance();
		bus.removeAllHandlers();
		handler = mock(CategoryChangeEventHandler.class);
		bus.addHandler(CategoryChangeEvent.class, handler);
	}

	@Test
	public void noSelection() {
		Selector selector = mockSelector();
		SelectorListener listener = new SelectorListener(selector, 0, bus,
				mock(ArchivariumMainPanel.class));

		when(selector.getSelectedRow()).thenReturn(-1);
		listener.valueChanged(mock(ListSelectionEvent.class));
		verify(handler, never()).changeCategory(anyObject(), anyString(),
				anyInt());

		when(selector.getSelectedRow()).thenReturn(10);
		listener.valueChanged(mock(ListSelectionEvent.class));
		verify(handler, never()).changeCategory(anyObject(), anyString(),
				anyInt());
	}

	@Test
	public void selectAll() {
		Selector selector = mockSelector();
		when(selector.getSelectedRow()).thenReturn(0);

		SelectorListener listener = new SelectorListener(selector, 0, bus,
				mock(ArchivariumMainPanel.class));
		listener.valueChanged(mock(ListSelectionEvent.class));

		verify(handler).changeCategory(any(), isNull(String.class), anyInt());
	}

	@Test
	public void selectSingleCategory() {
		Selector selector = mockSelector();
		when(selector.getSelectedRow()).thenReturn(2);

		SelectorListener listener = new SelectorListener(selector, 0, bus,
				mock(ArchivariumMainPanel.class));
		listener.valueChanged(mock(ListSelectionEvent.class));

		verify(handler).changeCategory(any(), same(VALUE), anyInt());
	}

	private Selector mockSelector() {
		Selector selector = mock(Selector.class);
		when(selector.getRowCount()).thenReturn(5);
		when(selector.getValueAt(2, 0)).thenReturn(VALUE);
		return selector;
	}
}
