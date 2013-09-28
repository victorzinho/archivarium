package org.archivarium.ui.events;

import geomatico.events.EventHandler;

public interface CategoryChangeEventHandler extends EventHandler {
	/**
	 * Called when the data category has been changed.
	 * 
	 * @param source
	 *            The object that fired the change.
	 * @param category
	 *            The new category value.
	 * @param column
	 *            The index of the changed category.
	 */
	void changeCategory(Object source, String category, int column);
}
