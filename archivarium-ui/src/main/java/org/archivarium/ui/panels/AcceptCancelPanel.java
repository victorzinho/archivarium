package org.archivarium.ui.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class AcceptCancelPanel extends JPanel {
	public AcceptCancelPanel(JComponent root, final Listener listener,
			Icon acceptIcon, Icon cancelIcon) {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(0, 5, 20, 5));

		JPanel buttons = new JPanel();

		JButton accept = new JButton(acceptIcon);
		accept.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.accept();
			}
		});
		buttons.add(accept);

		JButton cancel = new JButton(cancelIcon);
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.cancel();
			}
		});
		buttons.add(cancel);

		add(root, BorderLayout.CENTER);
		add(buttons, BorderLayout.SOUTH);
	}

	public static interface Listener {
		void accept();

		void cancel();
	}
}
