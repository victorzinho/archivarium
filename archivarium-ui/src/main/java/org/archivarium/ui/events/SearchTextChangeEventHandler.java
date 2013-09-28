package org.archivarium.ui.events;

import geomatico.events.EventHandler;

public interface SearchTextChangeEventHandler extends EventHandler {
	/**
	 * Called when the search text has changed.
	 * 
	 * @param source
	 *            The object that fired the change.
	 * @param text
	 *            The new search text.
	 */
	void changeText(Object source, String text);
}
