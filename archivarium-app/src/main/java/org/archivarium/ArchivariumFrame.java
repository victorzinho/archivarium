package org.archivarium;

import geomatico.events.EventBus;
import geomatico.events.ExceptionEvent;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.table.TableColumn;

import org.archivarium.data.ScoreProviderDataSource;
import org.archivarium.data.ScoreRow;
import org.archivarium.data.ScoreSchema;
import org.archivarium.events.ScoreAddedEvent;
import org.archivarium.events.ScoreAddedHandler;
import org.archivarium.events.ScoreDeletedEvent;
import org.archivarium.events.ScoreDeletedHandler;
import org.archivarium.inject.ScoreDataFactory;
import org.archivarium.ui.UIFactory;
import org.archivarium.ui.data.DataHandlerException;
import org.archivarium.ui.events.CategoryChangeEvent;
import org.archivarium.ui.events.CategoryChangeEventHandler;
import org.archivarium.ui.events.SearchTextChangeEvent;
import org.archivarium.ui.events.SearchTextChangeEventHandler;
import org.archivarium.ui.panels.ArchivariumMainPanel;

import com.google.inject.name.Named;

public class ArchivariumFrame extends JFrame implements
		CategoryChangeEventHandler, SearchTextChangeEventHandler,
		ScoreAddedHandler, ScoreDeletedHandler {
	private static final URL icon = Launcher.class
			.getResource("archivarium.png");

	private ArchivariumMainPanel<ScoreRow> main;

	@Inject
	private EventBus eventBus;

	@Inject
	private ResourceBundle messages;

	@Inject
	private UIFactory uiFactory;

	@Inject
	private ScoreDataFactory dataFactory;

	@Inject
	@Named("local")
	private ScoreProvider provider;

	private ScoreProviderDataSource source;

	public void launch(List<String> fields, List<String> selectors)
			throws IOException, DataHandlerException, ScoreProviderException {
		setTitle("Archivarium");
		setIconImage(ImageIO.read(icon));

		int[] fieldIndexes = new int[fields.size()];
		for (int i = 0; i < fieldIndexes.length; i++) {
			String field = fields.get(i);
			int index = ScoreSchema.FIELD_NAMES.indexOf(field);
			if (index == -1) {
				throw new IllegalArgumentException("Invalid field:" + field);
			}
			fieldIndexes[i] = index;
		}

		int[] selectorIndexes = new int[selectors.size()];
		for (int i = 0; i < selectorIndexes.length; i++) {
			String selector = selectors.get(i);
			int index = fields.indexOf(selector) + 1;
			if (index == -1) {
				throw new IllegalArgumentException("Invalid field:" + selector);
			}
			selectorIndexes[i] = index;
		}

		ScoreSchema schema = new ScoreSchema(fieldIndexes);

		eventBus.addHandler(CategoryChangeEvent.class, this);
		eventBus.addHandler(ScoreDeletedEvent.class, this);
		eventBus.addHandler(ScoreAddedEvent.class, this);
		eventBus.addHandler(SearchTextChangeEvent.class, this);

		source = dataFactory.createSource(provider, schema);
		source.update();

		main = new ArchivariumMainPanel<ScoreRow>(source,
				dataFactory.createHandler(provider, schema), selectorIndexes,
				uiFactory, messages, eventBus);

		TableColumn tableColumn = main.getLocalDataPanel().getColumn(0);
		tableColumn.setPreferredWidth(50);
		tableColumn.setWidth(50);

		JPanel rootPanel = new JPanel(new BorderLayout());
		rootPanel.add(main, BorderLayout.CENTER);
		add(rootPanel);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void changeCategory(Object source, String category, int column) {
		if (source == main) {
			try {
				this.source.setCategory(category, column);
			} catch (ScoreProviderException e) {
				eventBus.fireEvent(new ExceptionEvent("Cannot update category",
						e));
			}
		}
	}

	@Override
	public void changeText(Object source, String text) {
		if (source == main) {
			try {
				this.source.setText(text);
			} catch (ScoreProviderException e) {
				eventBus.fireEvent(new ExceptionEvent("Cannot update scores", e));
			}
		}
	}

	@Override
	public void added(Score score) {
		sourceChange();
	}

	@Override
	public void deleted(Score score) {
		sourceChange();
	}

	private void sourceChange() {
		try {
			source.update();
		} catch (ScoreProviderException e) {
			eventBus.fireEvent(new ExceptionEvent("Cannot obtain scores", e));
		}
	}
}
