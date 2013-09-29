package org.archivarium.data;

import java.util.ArrayList;
import java.util.List;

import org.archivarium.Score;

public class ScoreFilter {
	private String text;

	public ScoreFilter(String text) {
		this.text = text;
	}

	public List<Score> filter(List<Score> scores) {
		if (text == null || text.length() == 0) {
			return scores;
		}

		List<Score> ret = new ArrayList<Score>();
		for (Score score : scores) {
			String name = score.getName();
			String author = score.getAuthor();
			String description = score.getDescription();
			String edition = score.getEdition();
			String location = score.getLocation();
			String genre = score.getGenre();
			if (name != null && name.toLowerCase().contains(text)) {
				ret.add(score);
			} else if (author != null && author.toLowerCase().contains(text)) {
				ret.add(score);
			} else if (description != null
					&& description.toLowerCase().contains(text)) {
				ret.add(score);
			} else if (edition != null && edition.toLowerCase().contains(text)) {
				ret.add(score);
			} else if (location != null
					&& location.toLowerCase().contains(text)) {
				ret.add(score);
			} else if (genre != null && genre.toLowerCase().contains(text)) {
				ret.add(score);
			} else {
				List<String> instruments = score.getInstruments();
				for (String instrument : instruments) {
					if (instrument.toLowerCase().contains(text)) {
						ret.add(score);
						break;
					}
				}
			}
		}
		return ret;
	}
}
