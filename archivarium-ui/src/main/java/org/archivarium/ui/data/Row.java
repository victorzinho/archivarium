package org.archivarium.ui.data;

public interface Row {
	/**
	 * Gets the data corresponding to the given column in this row.
	 * 
	 * @param column
	 *            The column with the data to obtain.
	 * @return The data in the given column for this row.
	 */
	Object getData(int column);

	/**
	 * Returns the identifier for this row.
	 * 
	 * @return the identifier for this row.
	 */
	int getId();

	/**
	 * Determines if this row has an associated resource that can be opened.
	 * 
	 * @return <code>true</code> if this row has an associated resource that can
	 *         be opened, <code>false</code> otherwise.
	 */
	boolean isOpenable();
}
