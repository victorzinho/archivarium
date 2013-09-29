package org.archivarium.ui.models;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import javax.swing.JTable;

import org.archivarium.ui.data.DataSource;
import org.junit.Before;
import org.junit.Test;

public class SelectorDataChangeHandlerTest {

	private ResourceBundle messages;
	private JTable table;
	private SelectorTableModel model;

	@Before
	public void setUp() {
		messages = ResourceBundle.getBundle("archivarium");
	}

	@Test
	public void differentSource() {
		JTable table = mock(JTable.class);
		SelectorTableModel model = mock(SelectorTableModel.class);

		SelectorDataChangeHandler handler = new SelectorDataChangeHandler(
				model, mock(DataSource.class), table, 0);
		handler.dataChanged(mock(DataSource.class));

		verify(model, never()).setValues(any(String[].class));
	}

	@Test
	public void sameValues() {
		JTable table = mock(JTable.class);
		String[] values = new String[] { "a", "b" };
		SelectorTableModel model = mock(SelectorTableModel.class);
		when(model.getValues()).thenReturn(values);

		DataSource<?> source = mock(DataSource.class);
		when(source.getUniqueValues(anyInt())).thenReturn(values);

		SelectorDataChangeHandler handler = new SelectorDataChangeHandler(
				model, source, table, 0);
		handler.dataChanged(source);

		verify(model).getValues();
		verify(model, never()).setValues(any(String[].class));
	}

	@Test
	public void noSelection() throws Exception {
		String[] prevValues = new String[] { "a", "b" };
		String[] newValues = new String[] { "a", "b", "c" };

		// Mock source
		DataSource<?> source = mock(DataSource.class);
		when(source.getUniqueValues(anyInt())).thenReturn(prevValues);

		// Create table and model from source
		SelectorTableModel model = new SelectorTableModel(source, 0, messages);
		JTable table = new JTable(model);

		// Modify source
		when(source.getUniqueValues(anyInt())).thenReturn(newValues);

		// Notify modification
		SelectorDataChangeHandler handler = new SelectorDataChangeHandler(
				model, source, table, 0);
		handler.dataChanged(source);

		// Check
		assertEquals(0, table.getSelectedRow());
		// +1 for "All"
		assertEquals(newValues.length + 1, table.getRowCount());
		assertArrayEquals(newValues, model.getValues());
	}

	@Test
	public void selectedBeforeAndAfter() throws Exception {
		String[] prevValues = new String[] { "a", "b", "c" };
		String[] newValues = new String[] { "a", "c" };

		testSelection(prevValues, newValues, 3);

		// Check selection
		assertEquals(2, table.getSelectedRow());
		// +1 for "All"
		assertEquals(newValues.length + 1, table.getRowCount());
		assertArrayEquals(newValues, model.getValues());
	}

	@Test
	public void selectedOnlyBefore() throws Exception {
		String[] prevValues = new String[] { "a", "b", "c" };
		String[] newValues = new String[] { "a", "c" };

		testSelection(prevValues, newValues, 2);

		assertEquals(0, table.getSelectedRow());
		assertEquals(newValues.length + 1, table.getRowCount());
		assertArrayEquals(newValues, model.getValues());
	}

	private void testSelection(String[] prevValues, String[] newValues,
			int selectionIndex) throws Exception {
		// Mock source
		DataSource<?> source = mock(DataSource.class);
		when(source.getUniqueValues(anyInt())).thenReturn(prevValues);

		// Create table and model from source
		model = new SelectorTableModel(source, 0, messages);
		table = new JTable(model);
		// Select 'b'
		table.getSelectionModel().setSelectionInterval(selectionIndex,
				selectionIndex);

		// Modify source
		when(source.getUniqueValues(anyInt())).thenReturn(newValues);

		// Notify modification
		SelectorDataChangeHandler handler = new SelectorDataChangeHandler(
				model, source, table, 0);
		handler.dataChanged(source);
	}

}
