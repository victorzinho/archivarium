package org.archivarium.inject;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.archivarium.ui.UIFactory;

public class DefaultUIFactory implements UIFactory {
	@Override
	public Icon getIcon(String resource) {
		return new ImageIcon(getClass().getResource("/icons/" + resource));
	}

	@Override
	public Color getSelectionForeground() {
		return Color.white;
	}

	@Override
	public Color getSelectionBackground() {
		return new Color(170, 205, 135);
	}

	@Override
	public Color getBackground() {
		return Color.white;
	}

	@Override
	public Color getForeground() {
		return Color.black;
	}

	@Override
	public Color getBackgroundAlternate() {
		return new Color(230, 230, 230);
	}
}
