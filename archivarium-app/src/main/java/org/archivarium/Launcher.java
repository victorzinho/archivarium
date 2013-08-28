package org.archivarium;

import geomatico.events.EventBus;
import geomatico.events.ExceptionEvent;
import geomatico.events.ExceptionEvent.Severity;
import geomatico.events.ExceptionEventHandler;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;

import org.archivarium.data.ScoreProviderDataHandler;
import org.archivarium.data.ScoreProviderDataSource;
import org.archivarium.impl.H2ScoreProvider;
import org.archivarium.ui.ArchivariumMainPanel;

public class Launcher implements Runnable {
	private static final URL icon = Launcher.class
			.getResource("archivarium.png");

	public static void main(String[] args) throws InvocationTargetException,
			InterruptedException {
		SwingUtilities.invokeAndWait(new Launcher());
	}

	private ExceptionHandler handler;

	@Override
	public void run() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to
			// another look and feel.
		}

		handler = new ExceptionHandler();
		EventBus.getInstance().addHandler(ExceptionEvent.class, handler);

		try {
			String database = H2ScoreProvider.class.getResource("scores.h2.db")
					.getFile().replaceAll("\\.h2\\.db", "");
			H2ScoreProvider provider = new H2ScoreProvider(database);

			ArchivariumMainPanel panel = new ArchivariumMainPanel(
					new ScoreProviderDataSource(provider),
					new ScoreProviderDataHandler(provider), new int[] { 2, 4,
							7 });

			JFrame frame = new JFrame();

			frame.setTitle("Archivarium");

			frame.setIconImage(ImageIO.read(icon));

			frame.add(panel);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private class ExceptionHandler implements ExceptionEventHandler {
		@Override
		public void exception(Severity severity, String message, Throwable t) {
			int messageType;
			String title;
			switch (severity) {
			case ERROR:
				messageType = JOptionPane.ERROR_MESSAGE;
				title = "Error";
				break;
			case WARNING:
				messageType = JOptionPane.WARNING_MESSAGE;
				title = "Warning";
				break;
			case INFO:
			default:
				messageType = JOptionPane.INFORMATION_MESSAGE;
				title = "Info";
				break;
			}
			JOptionPane.showMessageDialog(null, message, title, messageType);
		}
	}
}
