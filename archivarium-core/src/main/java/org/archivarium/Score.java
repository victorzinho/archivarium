package org.archivarium;

import java.util.List;

public interface Score {
	int getId();

	String getName();

	String getAuthor();

	String getDescription();

	List<String> getInstruments();

	String getEdition();

	String getURL();

	String getFormat();

	String getLocation();

	String getGenre();

	void setName(String name);

	void setAuthor(String author);

	void setDescription(String description);

	void setInstruments(List<String> instruments);

	void setEdition(String edition);

	void setURL(String url);

	void setFormat(String format);

	void setLocation(String location);

	void setGenre(String genre);
}
