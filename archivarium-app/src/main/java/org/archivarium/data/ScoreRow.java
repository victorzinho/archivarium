package org.archivarium.data;

import org.archivarium.Score;
import org.archivarium.ui.UIFactory;
import org.archivarium.ui.data.Row;

public class ScoreRow implements Row {
	private Score score;
	private ScoreSchema schema;
	private UIFactory factory;

	public ScoreRow(UIFactory factory, Score score, ScoreSchema schema) {
		this.factory = factory;
		this.score = score;
		this.schema = schema;
	}

	@Override
	public Object getData(int column) {
		if (column == 0) {
			if (score.getURL() == null || score.getURL().length() == 0) {
				return null;
			} else {
				String format = score.getFormat();
				if (format == null) {
					return null;
				} else if (format.equalsIgnoreCase("pdf")) {
					return factory.getIcon("pdf.png");
				} else if (format.equalsIgnoreCase("sib")) {
					return factory.getIcon("sib.png");
				} else {
					return format;
				}
			}
		} else {
			// -1 for the icon column
			return schema.getValue(score, column - 1);
		}
	}

	public Score getScore() {
		return score;
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
