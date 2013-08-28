package org.archivarium.ui.widgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import sun.misc.Launcher;

public class ButtonsCellRenderer extends AbstractCellEditor implements
		TableCellRenderer, TableCellEditor, MouseListener {
	private JTable table;
	private Object value;
	private ButtonInfo[] buttons;

	public ButtonsCellRenderer(JTable table, ButtonInfo[] buttons) {
		this.table = table;
		this.buttons = buttons;
	}

	private JPanel createPanel(boolean isSelected, boolean hasFocus, int row,
			int column) {
		JPanel panel = new JPanel();
		for (ButtonInfo info : buttons) {
			JButton button = new JButton();

			button.setFocusPainted(false);
			button.setName(info.imagePath);
			button.setIcon(loadIcon(info.imagePath));
			button.addActionListener(info.listener);
			button.setEnabled(info.enabled);

			panel.add(button);
		}

		if (isSelected) {
			panel.setForeground(table.getSelectionForeground());
			panel.setBackground(table.getSelectionBackground());
		} else {
			// Get a default component just to get colors
			TableCellRenderer reference = table
					.getDefaultRenderer(String.class);
			Component component = reference.getTableCellRendererComponent(
					table, "", isSelected, hasFocus, row, column);

			// update panel colors
			panel.setForeground(new Color(component.getForeground().getRGB()));
			panel.setBackground(new Color(component.getBackground().getRGB()));
		}
		return panel;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		return createPanel(isSelected, hasFocus, row, column);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		this.value = value;
		return createPanel(true, true, row, column);
	}

	@Override
	public Object getCellEditorValue() {
		return value;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		fireEditingStopped();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	private ImageIcon loadIcon(String path) {
		try {
			return new ImageIcon(ImageIO.read(Launcher.class
					.getResourceAsStream(path)));
		} catch (IOException e) {
			return null;
		}
	}
}
