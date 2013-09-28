package org.archivarium.ui.events;

import geomatico.events.Event;

import org.archivarium.ui.data.DataSource;

public class DataChangeEvent implements Event<DataChangeEventHandler> {
	private DataSource<?> source;

	public DataChangeEvent(DataSource<?> source) {
		this.source = source;
	}

	@Override
	public void dispatch(DataChangeEventHandler handler) {
		handler.dataChanged(source);
	}
}
