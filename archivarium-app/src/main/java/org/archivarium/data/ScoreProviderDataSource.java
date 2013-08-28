package org.archivarium.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

import org.archivarium.Score;
import org.archivarium.ScoreProvider;
import org.archivarium.ScoreProviderException;
import org.archivarium.impl.DefaultScore;
import org.archivarium.ui.data.TableDataException;
import org.archivarium.ui.data.DataSource;
import org.archivarium.ui.data.Row;

public class ScoreProviderDataSource implements DataSource {
	private static final ImageIcon ICON_SIB = new ImageIcon(
			ScoreProviderDataSource.class.getResource("/icons/sib.png"));
	private static final ImageIcon ICON_PDF = new ImageIcon(
			ScoreProviderDataSource.class.getResource("/icons/pdf.png"));
	private static final ImageIcon ICON_DEFAULT = new ImageIcon(
			ScoreProviderDataSource.class.getResource("/icons/default.png"));

	static final int COLUMN_INDEX_FORMAT = 0;
	static final int COLUMN_INDEX_NAME = 1;
	static final int COLUMN_INDEX_AUTHOR = 2;
	static final int COLUMN_INDEX_DESCRIPTION = 3;
	static final int COLUMN_INDEX_INSTRUMENTS = 4;
	static final int COLUMN_INDEX_EDITION = 5;
	static final int COLUMN_INDEX_LOCATION = 6;
	static final int COLUMN_INDEX_GENRE = 7;

	static final String[] COLUMNS = { "", "name", "author", "description",
			"instruments", "edition", "location", "genre" };

	private ScoreProvider provider;

	public ScoreProviderDataSource(ScoreProvider provider) {
		this.provider = provider;
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public String getColumnName(int column) throws IllegalArgumentException {
		if (column < 0 || column >= COLUMNS.length) {
			throw new IllegalArgumentException("Invalid column index: "
					+ column);
		}

		return COLUMNS[column];
	}

	@Override
	public Row getRowById(int id) throws TableDataException {
		try {
			return new ScoreRow(provider.getScoreById(id));
		} catch (ScoreProviderException e) {
			throw new TableDataException(e);
		}
	}

	@Override
	public String[][] getUniqueValues(String[] criteria, int fixedColumnIndex)
			throws TableDataException {
		return getUniqueValues();
	}

	@Override
	public String[][] getUniqueValues() throws TableDataException {
		try {
			List<Score> scores = provider.getScores();
			String[][] ret = new String[COLUMNS.length][];
			for (int i = 0; i < ret.length; i++) {
				Set<String> set = new HashSet<String>();
				String value = null;
				for (Score score : scores) {
					if (i == COLUMN_INDEX_NAME) {
						value = score.getName();
					} else if (i == COLUMN_INDEX_AUTHOR) {
						value = score.getAuthor();
					} else if (i == COLUMN_INDEX_DESCRIPTION) {
						value = score.getDescription();
					} else if (i == COLUMN_INDEX_INSTRUMENTS) {
						List<String> instruments = score.getInstruments();
						value = "";
						for (int j = 0; j < instruments.size(); j++) {
							if (j > 0) {
								value += ", ";
							}
							value += instruments.get(j);
						}
					} else if (i == COLUMN_INDEX_EDITION) {
						value = score.getEdition();
					} else if (i == COLUMN_INDEX_LOCATION) {
						value = score.getLocation();
					} else if (i == COLUMN_INDEX_GENRE) {
						value = score.getGenre();
					}

					if (value != null) {
						set.add(value);
					}
				}

				ret[i] = set.toArray(new String[set.size()]);
			}

			return ret;
		} catch (ScoreProviderException e) {
			throw new TableDataException(e);
		}
	}

	@Override
	public Row[] getRows(String[] criteria) throws TableDataException {
		if (criteria != null && criteria.length != COLUMNS.length) {
			throw new IllegalArgumentException("Invalid number of criteria");
		}

		List<Score> scores;
		try {
			if (criteria != null) {
				Score model = new DefaultScore();
				model.setName(criteria[COLUMN_INDEX_NAME]);
				model.setAuthor(criteria[COLUMN_INDEX_AUTHOR]);
				model.setDescription(criteria[COLUMN_INDEX_DESCRIPTION]);
				String instruments = criteria[COLUMN_INDEX_INSTRUMENTS];
				if (instruments != null) {
					model.setInstruments(Arrays.asList(instruments.split(", ")));
				}
				model.setEdition(criteria[COLUMN_INDEX_EDITION]);
				model.setLocation(criteria[COLUMN_INDEX_LOCATION]);
				model.setGenre(criteria[COLUMN_INDEX_GENRE]);

				scores = provider.search(model, true);
			} else {
				scores = provider.getScores();
			}
		} catch (ScoreProviderException e) {
			throw new TableDataException(e);
		}

		Row[] ret = new Row[scores.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = new ScoreRow(scores.get(i));
		}

		return ret;
	}

	private class ScoreRow implements Row {
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
				if (score.getURL() == null) {
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
				List<String> instruments = score.getInstruments();
				String ret = "";
				for (int i = 0; i < instruments.size(); i++) {
					if (i > 0) {
						ret += ", ";
					}
					ret += instruments.get(i);
				}
				return ret;
			} else if (column == COLUMN_INDEX_EDITION) {
				return score.getEdition();
			} else if (column == COLUMN_INDEX_LOCATION) {
				return score.getLocation();
			} else if (column == COLUMN_INDEX_GENRE) {
				return score.getGenre();
			} else {
				throw new RuntimeException("bug! Invalid column index: "
						+ column);
			}
		}

		@Override
		public int getId() {
			return score.getId();
		}
	}
}
