package org.archivarium.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.archivarium.Score;
import org.archivarium.data.ScoreRow;
import org.archivarium.impl.DefaultScore;
import org.archivarium.ui.data.RowEditionPanel;

public class ScoreEditionPanel extends JPanel implements
		RowEditionPanel<ScoreRow> {
	private ScoreRow row;

	private JTextField name, author, description, instruments, edition, format,
			location, genre;
	private FileField url;

	public ScoreEditionPanel() {
		setLayout(new GridBagLayout());

		// Create fields
		name = new JTextField();
		author = new JTextField();
		description = new JTextField();
		instruments = new JTextField();
		edition = new JTextField();
		url = new FileField(JFileChooser.FILES_ONLY);
		format = new JTextField();
		location = new JTextField();
		genre = new JTextField();

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.ipadx = 5;
		c.ipady = 5;

		// Labels
		c.anchor = GridBagConstraints.EAST;
		add(new JLabel("Name: "), c);
		c.gridy++;
		add(new JLabel("Author: "), c);
		c.gridy++;
		add(new JLabel("Description: "), c);
		c.gridy++;
		add(new JLabel("Instruments: "), c);
		c.gridy++;
		add(new JLabel("Edition: "), c);
		c.gridy++;
		add(new JLabel("URL: "), c);
		c.gridy++;
		add(new JLabel("Format: "), c);
		c.gridy++;
		add(new JLabel("Location"), c);
		c.gridy++;
		add(new JLabel("Genre: "), c);

		// Fields
		c.gridy = 0;
		c.gridx = 1;
		c.weightx = 1;
		add(name, c);
		c.gridy++;
		add(author, c);
		c.gridy++;
		add(description, c);
		c.gridy++;
		add(instruments, c);
		c.gridy++;
		add(edition, c);
		c.gridy++;
		add(url, c);
		c.gridy++;
		add(format, c);
		c.gridy++;
		add(location, c);
		c.gridy++;
		add(genre, c);

		// Fill
		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 2;
		c.weighty = 1;
		add(new JPanel(), c);
	}

	@Override
	public void setRow(ScoreRow row) {
		this.row = row;

		if (row == null) {
			name.setText("");
			author.setText("");
			description.setText("");
			edition.setText("");
			url.setFile(null);
			format.setText("");
			location.setText("");
			genre.setText("");
		} else {
			Score score = row.getScore();
			name.setText(score.getName());
			author.setText(score.getAuthor());
			description.setText(score.getDescription());
			edition.setText(score.getEdition());
			url.setFile(score.getURL());
			format.setText(score.getFormat());
			location.setText(score.getLocation());
			genre.setText(score.getGenre());
			instruments.setText(row.getInstrumentsString());
		}
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public ScoreRow getRow() {
		if (row == null) {
			row = new ScoreRow(new DefaultScore());
		}
		row.getScore().setName(name.getText().trim());
		row.getScore().setAuthor(author.getText().trim());
		row.getScore().setDescription(description.getText().trim());
		row.getScore().setEdition(edition.getText().trim());
		row.getScore().setURL(url.getFile().getAbsolutePath());
		row.getScore().setFormat(format.getText().trim());
		row.getScore().setLocation(location.getText().trim());
		row.getScore().setGenre(genre.getText().trim());

		List<String> instrumentList = new ArrayList<String>();
		String[] s = this.instruments.getText().split(",");
		for (String instrument : s) {
			instrumentList.add(instrument.trim());
		}
		row.getScore().setInstruments(instrumentList);
		return row;
	}
}
