package org.archivarium.ui.data;

public interface DataSource<T extends Row> {
	/**
	 * Returns the number of columns in this data source. It <b>does NOT</b>
	 * include the ID column.
	 * 
	 * @return the number of columns.
	 */
	int getColumnCount();

	/**
	 * Returns the name of the column with the given index.
	 * 
	 * @param column
	 *            The column index. It must be greater or equal to 0 and lower
	 *            than {@link #getColumnCount()}.
	 * @return The name of the column.
	 * @throws IllegalArgumentException
	 *             if the given index is not a valid index.
	 */
	String getColumnName(int column) throws IllegalArgumentException;

	/**
	 * Returns all the rows in this data source.
	 * 
	 * @return All the rows.
	 */
	T[] getRows();

	/**
	 * Returns the row with the given id.
	 * 
	 * @param id
	 *            The id of the row to obtain.
	 * @return The row with the given id or <code>null</code> if no row has the
	 *         given id.
	 */
	T getRowById(int id) throws DataHandlerException;

	/**
	 * Returns the unique values in this data source for the given column.
	 * 
	 * @return An array containing all the unique values.
	 */
	String[] getUniqueValues(int column);
}
