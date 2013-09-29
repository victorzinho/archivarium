package org.archivarium.ui.panels;

import geomatico.events.EventBus;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.archivarium.ui.UIFactory;
import org.archivarium.ui.data.DataHandler;
import org.archivarium.ui.data.DataHandlerException;
import org.archivarium.ui.data.DataSource;
import org.archivarium.ui.data.Row;
import org.archivarium.ui.data.RowEditionPanel;
import org.archivarium.ui.events.SearchTextChangeEvent;
import org.archivarium.ui.listeners.AddRowPanelListener;
import org.archivarium.ui.listeners.UpdateRowPanelListener;
import org.archivarium.ui.models.MainTableModel;

public class ArchivariumMainPanel<T extends Row> extends JPanel implements
		ChangeListener, DocumentListener {
	public enum Tab {
		LOCAL, ALL, UPDATE, ADD
	}

	private DataHandler<T> handler;
	private UIFactory factory;
	private EventBus eventBus;

	private JPanel rootPanel, toolbar;
	private CardLayout layout;

	private JTextField search;
	private JToggleButton local, all, update, add;

	private LocalDataPanel<T> localDataPanel;
	private AllDataPanel allDataPanel;

	public ArchivariumMainPanel(DataSource<T> source, DataHandler<T> handler,
			int[] columnSelectors, UIFactory factory, ResourceBundle messages,
			EventBus eventBus) throws DataHandlerException {
		this.handler = handler;
		this.factory = factory;
		this.eventBus = eventBus;

		// Create root panel
		layout = new CardLayout();
		rootPanel = new JPanel(layout);

		localDataPanel = new LocalDataPanel<T>(source, handler,
				columnSelectors, this, factory, messages, eventBus);
		allDataPanel = new AllDataPanel();

		rootPanel.add(localDataPanel, Tab.LOCAL.toString());
		rootPanel.add(allDataPanel, Tab.ALL.toString());
		rootPanel.add(
				createUpdatePanel(new AddRowPanelListener<T>(this, eventBus),
						null), Tab.ADD.toString());

		// Create toolbar
		toolbar = new JPanel();

		search = new JTextField(20);
		search.getDocument().addDocumentListener(this);

		toolbar.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 0;

		JLabel clear = new JLabel(factory.getIcon("clear.png"));
		clear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				search.setText("");
			}
		});

		ButtonGroup group = new ButtonGroup();
		local = createToggleButton(factory.getIcon("bass_clef.png"), group);
		all = createToggleButton(factory.getIcon("world.png"), group);
		add = createToggleButton(factory.getIcon("add.png"), group);
		update = createToggleButton(factory.getIcon("update.png"), group);

		update.setVisible(false);
		all.setEnabled(false);
		local.setSelected(true);

		toolbar.add(local, c);
		c.gridx++;
		toolbar.add(all, c);
		c.gridx++;
		toolbar.add(add, c);
		c.gridx++;
		toolbar.add(update, c);
		c.weightx = 1;
		c.gridx++;
		toolbar.add(new JPanel(), c);
		c.weightx = 0;
		c.gridx++;
		toolbar.add(search, c);
		c.gridx++;
		toolbar.add(clear, c);

		toolbar.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

		setLayout(new BorderLayout());
		add(toolbar, BorderLayout.NORTH);
		add(rootPanel, BorderLayout.CENTER);
	}

	private JToggleButton createToggleButton(Icon icon, ButtonGroup group) {
		JToggleButton button = new JToggleButton(icon);
		button.addChangeListener(this);
		button.setFocusable(false);
		group.add(button);
		return button;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (local.isSelected()) {
			layout.show(rootPanel, Tab.LOCAL.name());
		} else if (all.isSelected()) {
			layout.show(rootPanel, Tab.ALL.name());
		} else if (update.isVisible() && update.isSelected()) {
			layout.show(rootPanel, Tab.UPDATE.name());
		} else if (add.isSelected()) {
			layout.show(rootPanel, Tab.ADD.name());
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		fireTextChange();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		fireTextChange();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		fireTextChange();
	}

	private void fireTextChange() {
		eventBus.fireEvent(new SearchTextChangeEvent(this, search.getText()));
	}

	public void selectUpdateTab() throws DataHandlerException {
		UpdateRowPanelListener<T> listener = new UpdateRowPanelListener<T>(
				this, eventBus);
		AcceptCancelPanel updatePanel = createUpdatePanel(listener,
				localDataPanel.getDataSource().getRowById(getSelectedId()));
		rootPanel.add(updatePanel, Tab.UPDATE.toString());
		selectTab(Tab.UPDATE);
	}

	public void selectTab(Tab tab) {
		layout.show(rootPanel, tab.name());
		local.setSelected(tab.equals(Tab.LOCAL));
		add.setSelected(tab.equals(Tab.ADD));
		all.setSelected(tab.equals(Tab.ALL));
		update.setSelected(tab.equals(Tab.UPDATE));
		update.setVisible(tab.equals(Tab.UPDATE));
	}

	private AcceptCancelPanel createUpdatePanel(
			AcceptCancelPanel.Listener listener, T row) {
		RowEditionPanel<T> rowEditionPanel = handler.getRowEditionPanel();
		rowEditionPanel.setRow(row);
		return new AcceptCancelPanel(rowEditionPanel.getComponent(), listener,
				factory.getIcon("accept.png"), factory.getIcon("cancel.png"));
	}

	public void closeUpdateTab(RowEditionPanel<T> rowEditionPanel) {
		JComponent comp = rowEditionPanel.getComponent();

		update.setVisible(false);
		layout.removeLayoutComponent(comp);
		rootPanel.remove(comp);

		local.setSelected(true);
		layout.show(rootPanel, Tab.LOCAL.name());
	}

	public int getSelectedId() {
		JTable table = localDataPanel.getTable();
		return (Integer) table.getValueAt(table.getSelectedRow(),
				MainTableModel.COLUMN_ID);
	}

	public LocalDataPanel<T> getLocalDataPanel() {
		return localDataPanel;
	}

	public AllDataPanel getAllDataPanel() {
		return allDataPanel;
	}

	public DataHandler<T> getDataHandler() {
		return handler;
	}
}
