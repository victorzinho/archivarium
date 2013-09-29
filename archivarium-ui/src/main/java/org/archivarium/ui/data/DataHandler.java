package org.archivarium.ui.data;

public interface DataHandler<T extends Row> {
	/**
	 * Adds a new element with the given data
	 * 
	 * @param row
	 *            The row containing the data of the new element. The row
	 *            identifier will be ignored.
	 * @throws DataHandlerException
	 *             if any error occurs while adding the new element.
	 */
	void add(T row) throws DataHandlerException;

	/**
	 * Deletes the element with the specified identifier.
	 * 
	 * @param id
	 *            The identifier of the element to delete.
	 * @throws DataHandlerException
	 *             if any error occurs while deleting the element.
	 */
	void delete(int id) throws DataHandlerException;

	/**
	 * Updates the element with the given data. The element to update will be
	 * determined by using the row identifier.
	 * 
	 * @param row
	 *            The row containing the identifier and data of the element to
	 *            update.
	 * @throws DataHandlerException
	 *             if any error occurs while updating the element or if the row
	 *             identifier does not correspond to any existing element.
	 */
	void update(T row) throws DataHandlerException;

	/**
	 * Opens the resource associated with the given row
	 * 
	 * @param id
	 *            The identifier of the row to open
	 * @throws DataHandlerException
	 *             if any I/O error occurs while opening the resource or if
	 *             there is no resource associated with the given row.
	 */
	void open(int id) throws DataHandlerException;

	/**
	 * Returns the object with the necessary information to add an element.
	 * 
	 * @return The {@link RowEditionPanel} object
	 */
	RowEditionPanel<T> getAddPanel();

	/**
	 * Returns the object with the necessary information to add an element.
	 * 
	 * @return The {@link RowEditionPanel} object
	 */
	RowEditionPanel<T> getUpdatePanel();
}
