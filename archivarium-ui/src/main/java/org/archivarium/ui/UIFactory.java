package org.archivarium.ui;

import java.awt.Color;

import javax.swing.Icon;

public interface UIFactory {
	Icon getIcon(String resource);

	Color getSelectionForeground();

	Color getSelectionBackground();

	Color getBackground();

	Color getForeground();

	Color getBackgroundAlternate();
}
