package org.archivarium.ui.events;

import geomatico.events.Event;

public class CategoryChangeEvent implements Event<CategoryChangeEventHandler> {
	private String category;
	private int column;
	private Object source;

	public CategoryChangeEvent(Object source, String category, int column) {
		this.source = source;
		this.category = category;
		this.column = column;
	}

	@Override
	public void dispatch(CategoryChangeEventHandler handler) {
		handler.changeCategory(source, category, column);
	}
}
