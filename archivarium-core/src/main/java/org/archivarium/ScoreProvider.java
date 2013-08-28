package org.archivarium;

import java.util.List;

public interface ScoreProvider {
	List<Score> getScores() throws ScoreProviderException;

	Score getScore(int i) throws ScoreProviderException;

	Score getScoreById(int id) throws ScoreProviderException;

	boolean readOnly();

	void addScore(Score score) throws ScoreProviderException;

	void modifyScore(Score score) throws ScoreProviderException;

	void deleteScore(Score score) throws ScoreProviderException;

	List<Score> search(Score model, boolean strict)
			throws ScoreProviderException;
}
