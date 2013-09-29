package org.archivarium.events;

import geomatico.events.EventHandler;

import org.archivarium.Score;

public interface ScoreAddedHandler extends EventHandler {
	void added(Score score);
}
