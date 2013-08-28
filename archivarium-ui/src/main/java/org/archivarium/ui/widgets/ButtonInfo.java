package org.archivarium.ui.widgets;

import java.awt.event.ActionListener;

public class ButtonInfo {
	public String imagePath;
	public ActionListener listener;
	public boolean enabled;

	public ButtonInfo(String imagePath, ActionListener listener, boolean enabled) {
		this.imagePath = imagePath;
		this.listener = listener;
		this.enabled = enabled;
	}
}
