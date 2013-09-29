package org.archivarium.impl;

import org.apache.commons.lang.StringUtils;
import org.archivarium.Score;
import org.archivarium.ScoreProviderException;

public class ScoreComparator {
	public boolean matches(Score model, Score score)
			throws ScoreProviderException {
		try {
			compareStrings(model.getTitle(), score.getTitle());
			compareStrings(model.getComposer(), score.getComposer());
			compareStrings(model.getDescription(), score.getDescription());
			compareStrings(model.getEdition(), score.getEdition());

			if (model.getInstruments() != null) {
				for (String modelInstrument : model.getInstruments()) {
					boolean found = false;
					for (String scoreInstrument : score.getInstruments()) {
						try {
							compareStrings(modelInstrument, scoreInstrument);
							found = true;
							break;
						} catch (AssertionError e) {
						}
					}

					if (!found) {
						throw new AssertionError();
					}
				}
			}

			compareStrings(model.getURL(), score.getURL());
			compareStrings(model.getFormat(), score.getFormat());
			compareStrings(model.getLocation(), score.getLocation());
			compareStrings(model.getGenre(), score.getGenre());

			return true;
		} catch (AssertionError e) {
			return false;
		}
	}

	private void compareStrings(String model, String score)
			throws AssertionError {
		int threshold = 3;
		if (model != null && score != null) {
			int dist = StringUtils.getLevenshteinDistance(model, score);
			if (dist > threshold) {
				throw new AssertionError();
			}
		}
	}
}
