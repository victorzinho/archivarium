package org.archivarium;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;

import org.archivarium.ui.ArchivariumMainPanel;
import org.archivarium.ui.data.TableDataException;
import org.archivarium.ui.data.DataHandler;
import org.archivarium.ui.data.DataSource;
import org.archivarium.ui.data.Row;

public class TestGUI implements Runnable {
	public static void main(String[] args) throws Exception {
		SwingUtilities.invokeLater(new TestGUI());
	}

	@Override
	public void run() {
		JFrame frame = new JFrame();
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to
			// another look and feel.
		}

		MockDataSource source = new MockDataSource();
		MockDataHandler handler = new MockDataHandler();
		int[] columnSelectors = new int[] { 1, 2, 0 };

		try {
			ArchivariumMainPanel panel = new ArchivariumMainPanel(source,
					handler, columnSelectors);
			frame.add(panel);
		} catch (TableDataException e) {
			throw new RuntimeException(e);
		}

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private String[][] data;
	private String[] columnNames;

	private TestGUI() {
		data = new String[3][4];
		data[0][0] = "myName";
		data[0][1] = "description...";
		data[0][2] = "zinho";
		data[0][3] = "(none)";
		data[1][0] = "";
		data[1][1] = "";
		data[1][2] = "";
		data[1][3] = "";
		data[1][0] = "";
		data[1][1] = "";
		data[1][2] = "";
		data[1][3] = "";
		data[2][0] = "";
		data[2][1] = "";
		data[2][2] = "";
		data[2][3] = "";
		columnNames = new String[] { "name", "desc", "author", "edition" };
	}

	private class MockDataHandler implements DataHandler {
		@Override
		public void remove(int id) {
			System.out.println("Remove " + data[id][0]);
		}

		@Override
		public void update(int id) {
			System.out.println("Update " + data[id][0]);

		}

		@Override
		public void open(int id) {
			System.out.println("Open " + data[id][0]);
		}
	}

	private class MockDataSource implements DataSource {
		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public Row getRowById(final int row) {
			return new Row() {

				@Override
				public int getId() {
					return row;
				}

				@Override
				public String getData(int column) {
					return data[row][column];
				}
			};
		}

		@Override
		public String[][] getUniqueValues(String[] criteria,
				int fixedColumnIndex) {
			String[][] unique = new String[][] { new String[] { "myName" },
					new String[] { "description" }, new String[] { "zinho" },
					new String[] { "(none)" }, };
			return unique;
		}

		@Override
		public String[][] getUniqueValues() {
			String[][] unique = new String[][] { new String[] { "myName" },
					new String[] { "description" }, new String[] { "zinho" },
					new String[] { "(none)" }, };
			return unique;
		}

		@Override
		public Row[] getRows(String[] criteria) {
			Row[] ret = new Row[data.length];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = getRowById(i);
			}
			return ret;
		}
	}

}
