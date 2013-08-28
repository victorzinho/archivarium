package org.archivarium;

import java.util.List;

public interface Score {
	enum Field {
		ID, NAME, AUTHOR
	}
	int ID = 0;
	int NAME = 1;
	int AUTHOR = 2;
	int DESCRIPTION = 3;
	int INSTRUMENTS = 4;
	int EDITION = 5;
	int URL = 6;
	int FORMAT = 7;
	int LOCATION = 8;
	int GENRE = 9;

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
