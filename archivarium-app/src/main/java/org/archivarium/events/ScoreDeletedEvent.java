package org.archivarium.events;

import geomatico.events.Event;

import org.archivarium.Score;

public class ScoreDeletedEvent implements Event<ScoreDeletedHandler> {
	private Score score;

	public ScoreDeletedEvent(Score score) {
		this.score = score;
	}

	@Override
	public void dispatch(ScoreDeletedHandler handler) {
		handler.deleted(score);
	}
}
