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
	T getRowById(int id);

	/**
	 * Returns all the unique values in this data source, for each different
	 * column.
	 * 
	 * @return An 2D array containing all the unique values. Column and row
	 *         indexes are transposed; for example,
	 *         <code>array[0][0..n-1]</code> contains the unique values for the
	 *         first column (given that <code>array</code> is the result of this
	 *         method).
	 */
	String[][] getUniqueValues();
}
