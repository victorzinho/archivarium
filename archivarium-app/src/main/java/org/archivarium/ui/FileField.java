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

public class FileField extends JPanel implements ActionListener {
	private JTextField fileField;
	private int selectionMode;

	public FileField(int selectionMode) {
		this.selectionMode = selectionMode;

		setLayout(new GridBagLayout());

		fileField = new JTextField();
		fileField.setEditable(false);
		fileField.setFocusable(false);

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
		return new File(fileField.getText());
	}

	public void setFile(String file) {
		if (file == null) {
			this.fileField.setText("");
		} else {
			this.fileField.setText(file);
		}
	}
}
