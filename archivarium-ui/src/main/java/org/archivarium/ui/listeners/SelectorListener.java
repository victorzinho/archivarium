package org.archivarium.ui.listeners;

import geomatico.events.EventBus;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.archivarium.ui.events.CategoryChangeEvent;
import org.archivarium.ui.panels.LocalDataPanel;
import org.archivarium.ui.panels.Selector;

public class SelectorListener implements ListSelectionListener {
	private int column;
	private Selector selector;
	private EventBus eventBus;

	public SelectorListener(Selector selector, int column, EventBus eventBus) {
		this.column = column;
		this.selector = selector;
		this.eventBus = eventBus;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int selected = selector.getSelectedRow();
		if (selected < 0 || selected >= selector.getRowCount()) {
			return;
		}

		String category;
		if (selected == 0) {
			// index == 0 means "All", so we remove the criterion
			category = null;
		} else {
			category = selector.getValueAt(selected, 0).toString();
		}

		eventBus.fireEvent(new CategoryChangeEvent(LocalDataPanel.class,
				category, column));
	}
}