package org.archivarium.ui.data;

public interface DataSource {
	/**
	 * NOT including ID
	 * 
	 * @return
	 * @throws TableDataException
	 */
	int getColumnCount();

	String getColumnName(int column) throws IllegalArgumentException;

	Row getRowById(int id) throws TableDataException;

	String[][] getUniqueValues(String[] criteria, int fixedColumnIndex)
			throws TableDataException;

	String[][] getUniqueValues() throws TableDataException;

	Row[] getRows(String[] criteria) throws TableDataException;
}
