package org.archivarium.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class FileField extends JPanel implements ActionListener {
	private JTextField fileField;
	private int selectionMode;

	public FileField(int selectionMode, final ChangeListener listener) {
		this.selectionMode = selectionMode;

		setLayout(new GridBagLayout());

		fileField = new JTextField();
		fileField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				listener.stateChanged(new ChangeEvent(FileField.this));
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				listener.stateChanged(new ChangeEvent(FileField.this));
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				listener.stateChanged(new ChangeEvent(FileField.this));
			}
		});

		JButton browse = new JButton("...");
		browse.addActionListener(this);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 0, 5);
		c.ipadx = 5;
		c.ipady = 5;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		add(fileField, c);
		c.weightx = 0;
		c.gridx++;
		c.insets = new Insets(0, 5, 0, 0);
		add(browse);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(selectionMode);
		if (JFileChooser.APPROVE_OPTION == fc.showOpenDialog(null)) {
			File f = fc.getSelectedFile();
			fileField.setText(f.getAbsolutePath());
		}
	}

	public File getFile() {
		if (fileField.getText() == null || fileField.getText().length() == 0) {
			return null;
		} else {
			return new File(fileField.getText());
		}
	}

	public void setFile(String file) {
		if (file == null) {
			this.fileField.setText("");
		} else {
			this.fileField.setText(file);
		}
	}
}
