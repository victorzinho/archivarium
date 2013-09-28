package org.archivarium.ui.data;

import javax.swing.JComponent;

/**
 * Implementations of this interface provide a graphical component to add/edit
 * an element using a {@link Row} to store the data.
 */
public interface RowEditionPanel<T extends Row> {
	/**
	 * Sets the data to show in the graphical component.
	 * 
	 * @param row
	 *            The data to show. If <code>null</code>, the component will be
	 *            emptied.
	 */
	void setRow(T row);

	/**
	 * The graphical component used to add/edit an element.
	 * 
	 * @return
	 */
	JComponent getComponent();

	/**
	 * Returns the data currently represented by the graphical component.
	 * 
	 * @return The data currently represented by the graphical component.
	 */
	T getRow();
}
