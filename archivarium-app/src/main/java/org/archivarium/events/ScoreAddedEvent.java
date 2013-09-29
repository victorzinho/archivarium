package org.archivarium.events;

import geomatico.events.Event;

import org.archivarium.Score;

public class ScoreAddedEvent implements Event<ScoreAddedHandler> {
	private Score score;

	public ScoreAddedEvent(Score score) {
		this.score = score;
	}

	@Override
	public void dispatch(ScoreAddedHandler handler) {
		handler.added(score);
	}
}
