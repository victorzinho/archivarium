package org.archivarium;

import java.util.List;

public interface Score {
	int getId();

	String getTitle();

	String getComposer();

	String getDescription();

	List<String> getInstruments();

	String getEdition();

	String getArrangement();

	String getOrigin();

	String getURL();

	String getFormat();

	String getLocation();

	String getGenre();

	String getLyrics();

	String getLanguage();

	void setTitle(String title);

	void setComposer(String composer);

	void setDescription(String description);

	void setInstruments(List<String> instruments);

	void setEdition(String edition);

	void setArrangement(String arrangement);

	void setOrigin(String origin);

	void setURL(String url);

	void setFormat(String format);

	void setLocation(String location);

	void setGenre(String genre);

	void setLyrics(String lyrics);

	void setLanguage(String language);
}
