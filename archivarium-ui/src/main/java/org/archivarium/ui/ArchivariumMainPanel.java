package org.archivarium.ui;

import geomatico.events.EventBus;
import geomatico.events.ExceptionEvent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import org.archivarium.ui.data.TableDataException;
import org.archivarium.ui.data.DataHandler;
import org.archivarium.ui.data.DataSource;
import org.archivarium.ui.widgets.AlternateRowCellRenderer;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout.Divider;
import org.jdesktop.swingx.MultiSplitLayout.Leaf;
import org.jdesktop.swingx.MultiSplitLayout.Node;
import org.jdesktop.swingx.MultiSplitLayout.Split;

public class ArchivariumMainPanel extends JPanel {
	private static final String MAIN_PANE = "main";

	private DataSource source;
	private DataHandler handler;

	private JTable mainTable;
	private JTable[] selectorTables;
	private boolean updatingSelection;

	public ArchivariumMainPanel(DataSource source, DataHandler handler,
			int[] columnSelectors) throws TableDataException {
		this.source = source;
		this.handler = handler;

		setLayout(new BorderLayout());

		// Prepare selectors
		Split selectors = new Split();
		List<Node> children = selectors.getChildren();
		String[][] uniqueValues = source.getUniqueValues();

		selectorTables = new JTable[source.getColumnCount()];

		for (int i = 0; i < columnSelectors.length; i++) {
			// Prepare layout
			if (i > 0) {
				children.add(new Divider());
			}
			Leaf leaf = new Leaf(Integer.toString(i));
			leaf.setWeight(1.0 / columnSelectors.length);
			children.add(leaf);

			// Create table
			int column = columnSelectors[i];
			String columnName = source.getColumnName(column);
			ArchivariumSelectorTableModel model = new ArchivariumSelectorTableModel(
					columnName, uniqueValues[column]);
			final JTable table = new JTable(model);
			table.getSelectionModel().addListSelectionListener(
					new ListSelectionListener() {
						@Override
						public void valueChanged(ListSelectionEvent e) {
							try {
								processListSelection(table, e);
							} catch (TableDataException exception) {
								EventBus.getInstance().fireEvent(
										new ExceptionEvent(exception
												.getMessage(), exception));
							}
						}
					});

			initializeTable(table);
			table.setRowHeight(25);
			selectorTables[columnSelectors[i]] = table;
		}
		selectors.setChildren(children);

		// Create main table
		mainTable = new JTable();
		initializeTable(mainTable);
		mainTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					processMouseClick(e);
				} catch (TableDataException exception) {
					EventBus.getInstance().fireEvent(
							new ExceptionEvent(exception.getMessage(),
									exception));
				}
			}
		});
		mainTable.setModel(new ArchivariumScoreTableModel(source, null));
		mainTable.setRowHeight(35);
		mainTable.setAutoCreateRowSorter(true);

		TableColumn idColumn = mainTable.getColumnModel().getColumn(
				ArchivariumScoreTableModel.COLUMN_ID);
		idColumn.setMinWidth(0);
		idColumn.setMaxWidth(0);
		idColumn.setPreferredWidth(0);
		idColumn.setWidth(0);

		TableColumn format = mainTable.getColumnModel().getColumn(1);
		format.setMinWidth(30);
		format.setMaxWidth(30);
		format.setPreferredWidth(30);
		format.setWidth(30);

		Leaf mainPane = new Leaf(MAIN_PANE);
		mainPane.setWeight(1.0);

		Split mainSplit = new Split(selectors, new Divider(), mainPane);
		mainSplit.setRowLayout(false);

		JXMultiSplitPane splitPane = new JXMultiSplitPane();
		splitPane.setModel(mainSplit);
		splitPane.add(new JScrollPane(mainTable), MAIN_PANE);
		for (int i = 0; i < columnSelectors.length; i++) {
			JScrollPane comp = new JScrollPane(
					selectorTables[columnSelectors[i]]);
			comp.setPreferredSize(new Dimension(350, 250));
			splitPane.add(comp, Integer.toString(i));
		}

		add(splitPane, BorderLayout.CENTER);
	}

	private void initializeTable(JTable table) {
		table.setAutoCreateRowSorter(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setDefaultRenderer(Object.class, new AlternateRowCellRenderer());
		table.setFocusable(false);
	}

	private void processMouseClick(MouseEvent e) throws TableDataException {
		int row = mainTable.rowAtPoint(e.getPoint());
		if (row >= 0 && row < mainTable.getRowCount()) {
			mainTable.setRowSelectionInterval(row, row);
		} else {
			mainTable.clearSelection();
		}

		int rowindex = mainTable.getSelectedRow();
		if (rowindex < 0) {
			return;
		}

		if (e.getButton() == MouseEvent.BUTTON3) {
			JPopupMenu popup = new ArchivariumPopupMenu(mainTable, handler,
					source);
			popup.show(e.getComponent(), e.getX(), e.getY());
		} else if (e.getClickCount() == 2
				&& e.getButton() == MouseEvent.BUTTON1) {
			int id = (Integer) mainTable.getValueAt(mainTable.getSelectedRow(),
					ArchivariumScoreTableModel.COLUMN_ID);
			handler.open(source.getRowById(id).getId());
		}
	}

	private void processListSelection(JTable sourceTable, ListSelectionEvent e)
			throws TableDataException {
		if (updatingSelection) {
			return;
		}
		updatingSelection = true;

		int fixedColumnIndex = Arrays.asList(selectorTables).indexOf(
				sourceTable);
		int nCols = source.getColumnCount();
		String[] criteria = createCriteriaFromSelection();

		String[][] uniqueValues = source.getUniqueValues(criteria,
				fixedColumnIndex);
		for (int i = 0; i < nCols; i++) {
			JTable table = selectorTables[i];
			if (table == null || table == sourceTable) {
				continue;
			}

			ArchivariumSelectorTableModel model = (ArchivariumSelectorTableModel) table
					.getModel();
			model.setValues(uniqueValues[i]);

			for (int j = 0; j < model.getRowCount(); j++) {
				if (model.getValueAt(j, 0).equals(criteria[i])) {
					table.getSelectionModel().addSelectionInterval(j, j);
				}
			}
		}
		updatingSelection = false;

		ArchivariumScoreTableModel model = (ArchivariumScoreTableModel) mainTable
				.getModel();
		model.setCriteria(createCriteriaFromSelection());
	}

	private String[] createCriteriaFromSelection() throws TableDataException {
		String[] criteria = new String[source.getColumnCount()];
		for (int i = 0; i < source.getColumnCount(); i++) {
			JTable table = selectorTables[i];
			if (table == null) {
				continue;
			}

			int selected = table.getSelectedRow();
			if (selected > 0) {
				criteria[i] = (String) table.getValueAt(selected, 0);
			}
		}

		return criteria;
	}
}
