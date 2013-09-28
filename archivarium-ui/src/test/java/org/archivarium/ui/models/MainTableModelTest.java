package org.archivarium.ui.models;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import geomatico.events.EventBus;

import javax.swing.ImageIcon;

import org.archivarium.ui.data.DataSource;
import org.archivarium.ui.data.Row;
import org.archivarium.ui.events.DataChangeEvent;
import org.junit.Before;
import org.junit.Test;

public class MainTableModelTest {
	private static final String[] COLUMNS = new String[] { "one", "two" };
	private static final Row[] data = new Row[2];
	static {
		data[0] = mock(Row.class);
		when(data[0].getData(0)).thenReturn("string");
		when(data[0].getData(1)).thenReturn(new ImageIcon(""));
		when(data[0].getId()).thenReturn(0);

		data[1] = mock(Row.class);
		when(data[1].getData(0)).thenReturn("another string");
		when(data[1].getData(1)).thenReturn(new ImageIcon(""));
		when(data[1].getId()).thenReturn(1);
	}

	private MainTableModel model;
	private DataSource<Row> source;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		source = mock(DataSource.class);
		when(source.getColumnCount()).thenReturn(COLUMNS.length);
		for (int i = 0; i < COLUMNS.length; i++) {
			when(source.getColumnName(i)).thenReturn(COLUMNS[i]);
		}
		when(source.getRows()).thenReturn(data);

		model = new MainTableModel(source);
	}

	@Test
	public void getColumnCount() {
		assertEquals(COLUMNS.length + 1, model.getColumnCount());
	}

	@Test
	public void gGetColumnName() {
		for (int i = 0; i < COLUMNS.length; i++) {
			assertEquals(COLUMNS[i], model.getColumnName(i + 1));
		}
	}

	@Test
	public void getColumnClass() {
		assertEquals(Integer.class, model.getColumnClass(0));
		assertEquals(String.class, model.getColumnClass(1));
		assertEquals(ImageIcon.class, model.getColumnClass(2));
	}

	@Test
	public void getRowCount() {
		assertEquals(data.length, model.getRowCount());
	}

	@Test
	public void getValue() {
		assertEquals(data[0].getId(), model.getValueAt(0, 0));
		assertEquals(data[1].getData(0), model.getValueAt(1, 1));
	}

	@Test
	public void events() {
		assertEquals(data[1].getData(0), model.getValueAt(1, 1));

		String value = "change";
		when(data[1].getData(0)).thenReturn(value);
		EventBus.getInstance().fireEvent(new DataChangeEvent(source));
		assertEquals(value, model.getValueAt(1, 1));
	}
}
