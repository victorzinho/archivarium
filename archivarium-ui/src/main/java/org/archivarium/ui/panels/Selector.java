package org.archivarium.ui.panels;

import geomatico.events.EventBus;

import java.awt.Dimension;
import java.util.ResourceBundle;

import javax.swing.ListSelectionModel;

import org.archivarium.ui.UIFactory;
import org.archivarium.ui.data.DataHandlerException;
import org.archivarium.ui.data.DataSource;
import org.archivarium.ui.events.DataChangeEvent;
import org.archivarium.ui.listeners.SelectorListener;
import org.archivarium.ui.models.SelectorDataChangeHandler;
import org.archivarium.ui.models.SelectorTableModel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

public class Selector extends JXTable {
	private SelectorDataChangeHandler handler;

	public Selector(DataSource<?> source, int column,
			ArchivariumMainPanel<?> parent, UIFactory factory,
			ResourceBundle messages, EventBus eventBus)
			throws DataHandlerException {
		super();
		SelectorTableModel model = new SelectorTableModel(source, column,
				messages);
		setModel(model);

		handler = new SelectorDataChangeHandler(model, source, this, column);
		eventBus.addHandler(DataChangeEvent.class, handler);

		getSelectionModel().addListSelectionListener(
				new SelectorListener(this, column, eventBus, parent));

		setAutoCreateRowSorter(false);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setShowGrid(false);
		setIntercellSpacing(new Dimension(0, 0));
		setSelectionBackground(factory.getSelectionBackground());
		setSelectionForeground(factory.getSelectionForeground());
		setForeground(factory.getForeground());
		setHighlighters(HighlighterFactory.createAlternateStriping(
				factory.getBackground(), factory.getBackgroundAlternate()));
		setFocusable(false);
		getTableHeader().setReorderingAllowed(false);

		setRowHeight(25);
		getSelectionModel().setSelectionInterval(0, 0);
	}
}
