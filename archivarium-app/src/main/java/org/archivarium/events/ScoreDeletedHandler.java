package org.archivarium.events;

import geomatico.events.EventHandler;

import org.archivarium.Score;

public interface ScoreDeletedHandler extends EventHandler {
	void deleted(Score score);
}
