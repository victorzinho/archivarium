package org.archivarium.ui.panels;

import geomatico.events.EventBus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
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
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.MultiSplitLayout.Divider;
import org.jdesktop.swingx.MultiSplitLayout.Leaf;
import org.jdesktop.swingx.MultiSplitLayout.Node;
import org.jdesktop.swingx.MultiSplitLayout.Split;
import org.jdesktop.swingx.decorator.HighlighterFactory;

public class LocalDataPanel<T extends Row> extends JPanel {
	private static final String MAIN_PANE = "main";

	private JXTable table;
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
		table = new JXTable(model) {
			public boolean getScrollableTracksViewportWidth() {
				return getPreferredSize().width < getParent().getWidth();
			}
		};
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setFocusable(false);
		table.setRowHeight(35);
		table.setAutoCreateRowSorter(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setHorizontalScrollEnabled(true);
		table.setSelectionBackground(factory.getSelectionBackground());
		table.setSelectionForeground(factory.getSelectionForeground());
		table.setForeground(factory.getForeground());
		table.setHighlighters(HighlighterFactory.createAlternateStriping(
				factory.getBackground(), factory.getBackgroundAlternate()));
		table.putClientProperty(JXTable.USE_DTCR_COLORMEMORY_HACK, null);
		adjustTableColumns(table);

		table.addMouseListener(new ShowPopupListener<T>(table, mainPanel,
				factory, eventBus));

		TableColumn tableColumn = table.getColumnModel().getColumn(
				MainTableModel.COLUMN_ID);
		tableColumn.setMinWidth(0);
		tableColumn.setMaxWidth(0);
		tableColumn.setPreferredWidth(0);
		tableColumn.setWidth(0);

		// Main pane
		Leaf mainPane = new Leaf(MAIN_PANE);
		mainPane.setWeight(1.0);

		JScrollPane mainScroll = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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

	private void adjustTableColumns(JTable table) {
		for (int i = 0; i < table.getColumnCount(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);

			TableCellRenderer renderer = column.getHeaderRenderer();
			if (renderer == null) {
				renderer = table.getTableHeader().getDefaultRenderer();
			}

			Component c = renderer.getTableCellRendererComponent(table,
					column.getHeaderValue(), false, false, 0, 0);
			int width = c.getPreferredSize().width;

			for (int j = 1; j < table.getRowCount(); j++) {
				renderer = table.getCellRenderer(j, i);
				c = renderer.getTableCellRendererComponent(table,
						table.getValueAt(j, i), false, false, j, i);
				width = Math.max(width, c.getPreferredSize().width);
			}

			width += 30;
			column.setPreferredWidth(width);
		}
	}

	public TableColumn getColumn(int column) {
		return table.getColumnModel().getColumn(column + 1);
	}

	public JTable getTable() {
		return table;
	}

	public DataSource<T> getDataSource() {
		return source;
	}
}
