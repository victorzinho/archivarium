package org.archivarium.ui.events;

import geomatico.events.Event;

public class SearchTextChangeEvent implements
		Event<SearchTextChangeEventHandler> {
	private String text;
	private Object source;

	public SearchTextChangeEvent(Object source, String text) {
		this.source = source;
		this.text = text;
	}

	@Override
	public void dispatch(SearchTextChangeEventHandler handler) {
		handler.changeText(source, text);
	}
}
