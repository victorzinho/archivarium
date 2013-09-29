package org.archivarium.data;

import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.archivarium.Score;
import org.archivarium.ui.data.Row;

public class ScoreRow implements Row {
	private static final Icon ICON_SIB = new ImageIcon(
			ScoreRow.class.getResource("/icons/sib.png"));
	private static final Icon ICON_PDF = new ImageIcon(
			ScoreRow.class.getResource("/icons/pdf.png"));
	private static final Icon ICON_DEFAULT = new ImageIcon(
			ScoreRow.class.getResource("/icons/default.png"));

	public static final int COLUMN_INDEX_FORMAT = 0;
	public static final int COLUMN_INDEX_NAME = 1;
	public static final int COLUMN_INDEX_AUTHOR = 2;
	public static final int COLUMN_INDEX_DESCRIPTION = 3;
	public static final int COLUMN_INDEX_INSTRUMENTS = 4;
	public static final int COLUMN_INDEX_EDITION = 5;
	public static final int COLUMN_INDEX_LOCATION = 6;
	public static final int COLUMN_INDEX_GENRE = 7;

	static final String[] COLUMNS = { "", "name", "author", "description",
			"instruments", "edition", "location", "genre" };

	private Score score;

	public ScoreRow(Score score) {
		this.score = score;
	}

	@Override
	public Object getData(int column) {
		if (column < 0 || column >= COLUMNS.length) {
			throw new IllegalArgumentException("Invalid column index");
		}

		if (column == COLUMN_INDEX_FORMAT) {
			if (score.getURL() == null || score.getURL().length() == 0) {
				return null;
			} else {
				String format = score.getFormat();
				if (format == null) {
					return null;
				} else if (format.equalsIgnoreCase("pdf")) {
					return ICON_PDF;
				} else if (format.equalsIgnoreCase("sib")) {
					return ICON_SIB;
				} else {
					return ICON_DEFAULT;
				}
			}
		} else if (column == COLUMN_INDEX_NAME) {
			return score.getName();
		} else if (column == COLUMN_INDEX_AUTHOR) {
			return score.getAuthor();
		} else if (column == COLUMN_INDEX_DESCRIPTION) {
			return score.getDescription();
		} else if (column == COLUMN_INDEX_INSTRUMENTS) {
			return getInstrumentsString();
		} else if (column == COLUMN_INDEX_EDITION) {
			return score.getEdition();
		} else if (column == COLUMN_INDEX_LOCATION) {
			return score.getLocation();
		} else if (column == COLUMN_INDEX_GENRE) {
			return score.getGenre();
		} else {
			throw new RuntimeException("bug! Invalid column index: " + column);
		}
	}

	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}

	public String getInstrumentsString() {
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

	@Override
	public int getId() {
		return score.getId();
	}

	@Override
	public boolean isOpenable() {
		return score.getURL() != null;
	}
}
