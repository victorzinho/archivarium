package org.archivarium.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.archivarium.Score;
import org.archivarium.data.ScoreRow;
import org.archivarium.data.ScoreSchema;
import org.archivarium.impl.DefaultScore;
import org.archivarium.ui.data.RowEditionPanel;

public class ScoreEditionPanel extends JPanel implements
		RowEditionPanel<ScoreRow>, ChangeListener {
	private ScoreRow row;

	private JTextField[] fields;
	private FileField url;
	private JTextField format;

	private ScoreSchema schema;
	private UIFactory factory;

	public ScoreEditionPanel(ScoreSchema schema, UIFactory factory) {
		this.schema = schema;
		this.factory = factory;

		setLayout(new GridBagLayout());

		// Create fields
		fields = new JTextField[schema.getFieldCount()];
		for (int i = 0; i < fields.length; i++) {
			fields[i] = new JTextField();
		}
		url = new FileField(JFileChooser.FILES_ONLY, this);
		format = new JTextField();

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.ipadx = 5;
		c.ipady = 5;
		c.anchor = GridBagConstraints.EAST;

		for (int i = 0; i < fields.length; i++) {
			c.gridx = 0;
			c.weightx = 0;
			add(new JLabel(schema.getFieldName(i) + ": "), c);
			c.gridx = 1;
			c.weightx = 1;
			add(fields[i], c);
			c.gridy++;
		}

		c.gridx = 0;
		c.weightx = 0;
		add(new JLabel("URL: "), c);
		c.gridx++;
		c.weightx = 1;
		add(url, c);

		c.gridx = 0;
		c.weightx = 0;
		c.gridy++;
		add(new JLabel("Format: "), c);
		c.gridx++;
		c.weightx = 1;
		add(format, c);

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
			for (int i = 0; i < schema.getFieldCount(); i++) {
				fields[i].setText("");
			}
			url.setFile(null);
			format.setText("");
		} else {
			Score score = row.getScore();

			for (int i = 0; i < schema.getFieldCount(); i++) {
				Object data = schema.getValue(score, i);
				fields[i].setText(data == null ? "" : data.toString());
			}
			url.setFile(score.getURL());
			format.setText(score.getFormat());
			updateFormatField();
		}
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public ScoreRow getRow() {
		String[] values = new String[fields.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = fields[i].getText().trim();
		}

		if (row == null) {
			row = new ScoreRow(factory, new DefaultScore(), schema);
		}

		schema.updateScore(row.getScore(), values);
		File file = url.getFile();
		if (file != null) {
			String path = file.getAbsolutePath();
			row.getScore().setURL(path);
			row.getScore().setFormat(path.substring(path.lastIndexOf('.') + 1));
		} else {
			row.getScore().setURL(null);
			row.getScore().setFormat(null);
		}
		return row;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		updateFormatField();
	}

	private void updateFormatField() {
		File file = url.getFile();
		boolean hasExtension = file != null && file.getName().contains(".");
		format.setEditable(!hasExtension);
		format.setEnabled(!hasExtension);

		if (hasExtension) {
			String name = file.getName();
			format.setText(name.substring(name.lastIndexOf('.') + 1));
		}
	}
}
