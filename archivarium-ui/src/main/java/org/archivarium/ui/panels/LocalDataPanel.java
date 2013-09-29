package org.archivarium.ui.panels;

import geomatico.events.EventBus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import org.archivarium.ui.UIFactory;
import org.archivarium.ui.data.DataHandler;
import org.archivarium.ui.data.DataHandlerException;
import org.archivarium.ui.data.DataSource;
import org.archivarium.ui.data.Row;
import org.archivarium.ui.events.DataChangeEvent;
import org.archivarium.ui.listeners.ShowPopupListener;
import org.archivarium.ui.models.MainTableModel;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout.Divider;
import org.jdesktop.swingx.MultiSplitLayout.Leaf;
import org.jdesktop.swingx.MultiSplitLayout.Node;
import org.jdesktop.swingx.MultiSplitLayout.Split;

public class LocalDataPanel<T extends Row> extends JPanel {
	private static final String MAIN_PANE = "main";

	private JTable table;
	private DataSource<T> source;

	public LocalDataPanel(DataSource<T> source, DataHandler<T> handler,
			int[] columnSelectors, ArchivariumMainPanel<T> mainPanel,
			UIFactory factory, ResourceBundle messages, EventBus eventBus)
			throws DataHandlerException {
		this.source = source;

		setLayout(new BorderLayout());

		// Prepare selectors
		Split selectorsSplit = new Split();
		List<Node> children = selectorsSplit.getChildren();
		JTable[] selectorTables = new JTable[source.getColumnCount()];
		for (int i = 0; i < columnSelectors.length; i++) {
			if (i > 0) {
				children.add(new Divider());
			}
			Leaf leaf = new Leaf(Integer.toString(i));
			leaf.setWeight(1.0 / columnSelectors.length);
			children.add(leaf);
			Selector selector = new Selector(source, columnSelectors[i],
					mainPanel, factory, messages, eventBus);
			selectorTables[columnSelectors[i]] = selector;
		}
		selectorsSplit.setChildren(children);

		// Create main table
		MainTableModel model = new MainTableModel(source);
		eventBus.addHandler(DataChangeEvent.class, model);
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setDefaultRenderer(Object.class, factory.getTableCellRenderer());
		table.setFocusable(false);
		table.setRowHeight(35);
		table.setAutoCreateRowSorter(true);

		table.addMouseListener(new ShowPopupListener<T>(table, mainPanel,
				factory, eventBus));
		doSetColumnFixedWidth(MainTableModel.COLUMN_ID, 0);

		// Main pane
		Leaf mainPane = new Leaf(MAIN_PANE);
		mainPane.setWeight(1.0);

		JScrollPane mainScroll = new JScrollPane(table);
		mainScroll.getViewport().setBackground(Color.white);

		Split mainSplit = new Split(selectorsSplit, new Divider(), mainPane);
		mainSplit.setRowLayout(false);

		JXMultiSplitPane splitPane = new JXMultiSplitPane();
		splitPane.setModel(mainSplit);
		splitPane.add(mainScroll, MAIN_PANE);

		for (int i = 0; i < columnSelectors.length; i++) {
			JScrollPane comp = new JScrollPane(
					selectorTables[columnSelectors[i]]);
			comp.setPreferredSize(new Dimension(350, 250));
			comp.getViewport().setBackground(Color.white);
			splitPane.add(comp, Integer.toString(i));
		}

		add(splitPane, BorderLayout.CENTER);
	}

	public void setColumnFixedWidth(int column, int width) {
		// + 1 for ID column
		doSetColumnFixedWidth(column + 1, width);
	}

	private void doSetColumnFixedWidth(int column, int width) {
		TableColumn tableColumn = table.getColumnModel().getColumn(column);
		tableColumn.setMinWidth(width);
		tableColumn.setMaxWidth(width);
		tableColumn.setPreferredWidth(width);
		tableColumn.setWidth(width);
	}

	public JTable getTable() {
		return table;
	}

	public DataSource<T> getDataSource() {
		return source;
	}
}
