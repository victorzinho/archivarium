package org.archivarium.ui.data;

public interface DataHandler {
	void remove(int id) throws TableDataException;

	void update(int id) throws TableDataException;

	/**
	 * Opens the resource associated with the given row
	 * 
	 * @param id
	 *            The identifier of the row to open
	 * @throws TableDataException
	 *             if any I/O error occurs while opening the resource or if
	 *             there is no resource associated with the given row.
	 */
	void open(int id) throws TableDataException;
}
