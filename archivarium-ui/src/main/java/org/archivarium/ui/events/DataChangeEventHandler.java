package org.archivarium.ui.events;

import geomatico.events.EventHandler;

import org.archivarium.ui.data.DataSource;

public interface DataChangeEventHandler extends EventHandler {
	/**
	 * Called when the table data has changed.
	 * 
	 * @param source
	 *            The source containing the data that has changed.
	 */
	void dataChanged(DataSource<?> source);
}
