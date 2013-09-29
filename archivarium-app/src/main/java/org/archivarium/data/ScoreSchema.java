package org.archivarium.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.archivarium.Score;
import org.archivarium.impl.DefaultScore;

public class ScoreSchema {
	public static final int TITLE = 0;
	public static final int COMPOSER = 1;
	public static final int DESCRIPTION = 2;
	public static final int INSTRUMENTS = 3;
	public static final int EDITION = 4;
	public static final int ARRANGEMENT = 5;
	public static final int ORIGIN = 6;
	public static final int LOCATION = 7;
	public static final int GENRE = 8;
	public static final int LYRICS = 9;
	public static final int LANGUAGE = 10;

	public static final List<String> FIELD_NAMES = Arrays
			.asList(new String[] { "title", "composer", "description",
					"instruments", "edition", "arrangement", "origin",
					"location", "genre", "lyrics", "language" });

	private String[] fieldNames;
	private int[] fieldIndexes;

	public ScoreSchema(int[] fieldIndexes) {
		this.fieldIndexes = fieldIndexes;
		this.fieldNames = new String[fieldIndexes.length];
		for (int i = 0; i < fieldNames.length; i++) {
			fieldNames[i] = FIELD_NAMES.get(fieldIndexes[i]);
		}
	}

	public int getFieldCount() {
		return fieldNames.length;
	}

	public String getFieldName(int column) {
		if (column < 0 || column >= fieldNames.length) {
			throw new IllegalArgumentException(Integer.toString(column));
		}
		return fieldNames[column];
	}

	public Object getValue(Score score, int column) {
		if (column < 0 || column >= FIELD_NAMES.size()) {
			throw new IllegalArgumentException("Invalid column index");
		}

		int index = fieldIndexes[column];
		if (index == TITLE) {
			return score.getTitle();
		} else if (index == COMPOSER) {
			return score.getComposer();
		} else if (index == DESCRIPTION) {
			return score.getDescription();
		} else if (index == INSTRUMENTS) {
			return getInstrumentsString(score);
		} else if (index == EDITION) {
			return score.getEdition();
		} else if (index == ARRANGEMENT) {
			return score.getArrangement();
		} else if (index == ORIGIN) {
			return score.getOrigin();
		} else if (index == LOCATION) {
			return score.getLocation();
		} else if (index == GENRE) {
			return score.getGenre();
		} else if (index == LYRICS) {
			return score.getLyrics();
		} else if (index == LANGUAGE) {
			return score.getLanguage();
		} else {
			throw new RuntimeException("bug! Invalid column index: " + column);
		}
	}

	public Score createScore(String[] values) {
		Score score = new DefaultScore();
		updateScore(score, values);
		return score;
	}

	public void updateScore(Score score, String[] values) {
		if (values == null || values.length != fieldIndexes.length) {
			throw new IllegalArgumentException("Invalid values array");
		}

		for (int i = 0; i < values.length; i++) {
			int index = fieldIndexes[i];
			if (index == TITLE) {
				score.setTitle(values[i]);
			} else if (index == COMPOSER) {
				score.setComposer(values[i]);
			} else if (index == DESCRIPTION) {
				score.setDescription(values[i]);
			} else if (index == INSTRUMENTS) {
				score.setInstruments(getInstrumentList(values[i]));
			} else if (index == EDITION) {
				score.setEdition(values[i]);
			} else if (index == ARRANGEMENT) {
				score.setArrangement(values[i]);
			} else if (index == ORIGIN) {
				score.setOrigin(values[i]);
			} else if (index == LOCATION) {
				score.setLocation(values[i]);
			} else if (index == GENRE) {
				score.setGenre(values[i]);
			} else if (index == LYRICS) {
				score.setLyrics(values[i]);
			} else if (index == LANGUAGE) {
				score.setLanguage(values[i]);
			} else {
				throw new RuntimeException("bug! Invalid column index: " + i);
			}
		}
	}

	public boolean matches(Score score, String text) {
		for (int i = 0; i < fieldIndexes.length; i++) {
			Object value = getValue(score, i);
			if (value == null) {
				continue;
			}
			if (value.toString().toLowerCase().contains(text.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	private List<String> getInstrumentList(String s) {
		List<String> ret = new ArrayList<String>();
		if (s != null) {
			String[] instruments = s.split(",");
			for (String instrument : instruments) {
				ret.add(instrument.trim());
			}
		}
		return ret;
	}

	private String getInstrumentsString(Score score) {
		List<String> instruments = score.getInstruments();
		String ret = "";
		if (instruments != null) {
			for (int i = 0; i < instruments.size(); i++) {
				if (i > 0) {
					ret += ", ";
				}
				ret += instruments.get(i);
			}
		}
		return ret;
	}
}
