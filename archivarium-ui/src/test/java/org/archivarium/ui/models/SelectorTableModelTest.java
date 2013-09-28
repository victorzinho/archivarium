package org.archivarium.ui.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import org.archivarium.ui.data.DataSource;
import org.archivarium.ui.data.Row;
import org.junit.Before;
import org.junit.Test;

public class SelectorTableModelTest {
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

	private SelectorTableModel model;
	private DataSource<Row> source;
	private int index;
	private ResourceBundle messages;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		source = mock(DataSource.class);
		when(source.getColumnCount()).thenReturn(COLUMNS.length);
		for (int i = 0; i < COLUMNS.length; i++) {
			when(source.getColumnName(i)).thenReturn(COLUMNS[i]);
		}
		when(source.getRows()).thenReturn(data);
		when(source.getUniqueValues()).thenReturn(
				new String[][] { { "string", "another string" }, {} });

		index = 0;
		messages = ResourceBundle.getBundle("archivarium");
		model = new SelectorTableModel(source, index, messages);
	}

	@Test
	public void getColumnCount() {
		assertEquals(1, model.getColumnCount());
	}

	@Test
	public void getColumnName() {
		assertEquals(COLUMNS[index], model.getColumnName(0));
	}

	@Test
	public void getRowCount() {
		assertEquals(data.length + 1, model.getRowCount());
	}

	@Test
	public void getValue() {
		assertEquals(messages.getString("all"), model.getValueAt(0, 0));
		assertEquals(data[0].getData(index), model.getValueAt(1, 0));
	}

	@Test
	public void setValues() {
		assertEquals(data[0].getData(0), model.getValueAt(1, 0));

		model.setValues(new String[] { "changed string", "another string" });
		assertEquals("changed string", model.getValueAt(1, 0));
	}

	@Test
	public void isEditable() {
		assertFalse(model.isCellEditable(0, 0));
	}
}
