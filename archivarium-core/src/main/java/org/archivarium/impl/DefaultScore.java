package org.archivarium.impl;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.archivarium.Score;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "scores")
public class DefaultScore implements Score {
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;

	private String title, composer, description, edition, arrangement, origin,
			format, location, genre, lyrics, language;
	private String url;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "score_instruments", joinColumns = @JoinColumn(name = "score_id"))
	@Column(name = "instrument")
	private List<String> instruments;

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getComposer() {
		return composer;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public List<String> getInstruments() {
		return instruments;
	}

	@Override
	public String getEdition() {
		return edition;
	}

	@Override
	public String getArrangement() {
		return arrangement;
	}

	@Override
	public String getOrigin() {
		return origin;
	}

	@Override
	public String getURL() {
		return url;
	}

	@Override
	public String getFormat() {
		return format;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public String getGenre() {
		return genre;
	}

	@Override
	public String getLyrics() {
		return lyrics;
	}

	@Override
	public String getLanguage() {
		return language;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void setTitle(String name) {
		this.title = name;
	}

	@Override
	public void setComposer(String composer) {
		this.composer = composer;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setEdition(String edition) {
		this.edition = edition;
	}

	@Override
	public void setArrangement(String arrangement) {
		this.arrangement = arrangement;
	}

	@Override
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	@Override
	public void setFormat(String format) {
		this.format = format;
	}

	@Override
	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public void setGenre(String genre) {
		this.genre = genre;
	}

	@Override
	public void setURL(String url) {
		this.url = url;
	}

	@Override
	public void setInstruments(List<String> instruments) {
		this.instruments = instruments;
	}

	@Override
	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}

	@Override
	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Score)) {
			return false;
		}

		Score score = (Score) obj;

		if (id != score.getId()) {
			return false;
		}

		try {
			checkField(title, score.getTitle());
			checkField(composer, score.getComposer());
			checkField(description, score.getDescription());
			checkField(edition, score.getEdition());
			checkField(arrangement, score.getArrangement());
			checkField(origin, score.getOrigin());
			checkField(url, score.getURL());
			checkField(format, score.getFormat());
			checkField(location, score.getLocation());
			checkField(genre, score.getGenre());
			checkField(lyrics, score.getLyrics());
			checkField(language, score.getLanguage());
		} catch (AssertionError e) {
			return false;
		}

		if (instruments != null) {
			if (score.getInstruments() == null) {
				return false;
			} else if (instruments.size() != score.getInstruments().size()) {
				return false;
			}
		} else {
			return score.getInstruments() == null;
		}

		for (int i = 0; i < instruments.size(); i++) {
			if (!instruments.get(i).equals(score.getInstruments().get(i))) {
				return false;
			}
		}

		return true;
	}

	private void checkField(String thisField, String otherField)
			throws AssertionError {
		if (thisField != null) {
			if (!thisField.equals(otherField)) {
				throw new AssertionError();
			}
		} else if (otherField != null) {
			throw new AssertionError();
		}
	}

	@Override
	public int hashCode() {
		int code = id;
		if (title != null) {
			code += title.hashCode();
		}
		if (composer != null) {
			code += composer.hashCode();
		}
		if (description != null) {
			code += description.hashCode();
		}
		if (instruments != null) {
			code += instruments.hashCode();
		}
		if (edition != null) {
			code += edition.hashCode();
		}
		if (arrangement != null) {
			code += arrangement.hashCode();
		}
		if (origin != null) {
			code += origin.hashCode();
		}
		if (url != null) {
			code += url.hashCode();
		}
		if (format != null) {
			code += format.hashCode();
		}
		if (location != null) {
			code += location.hashCode();
		}
		if (genre != null) {
			code += genre.hashCode();
		}
		if (lyrics != null) {
			code += lyrics.hashCode();
		}
		if (language != null) {
			code += language.hashCode();
		}

		return code;
	}
}
