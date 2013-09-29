package org.archivarium;

import geomatico.events.EventBus;
import geomatico.events.ExceptionEvent;
import geomatico.events.ExceptionEvent.Severity;
import geomatico.events.ExceptionEventHandler;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;
import org.archivarium.inject.ArchivariumModule;
import org.h2.tools.Backup;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Launcher implements Runnable {
	private static final Logger logger = Logger.getLogger(Launcher.class);

	private static Injector injector;

	public static void main(String[] args) throws InvocationTargetException,
			InterruptedException {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				JOptionPane.showMessageDialog(null, "ERROR!!!");
			}
		});

		injector = Guice.createInjector(new ArchivariumModule());
		SwingUtilities.invokeAndWait(injector.getInstance(Launcher.class));
	}

	private ExceptionEventHandler exceptionHandler;
	private ArchivariumFrame frame;

	@Inject
	private EventBus eventBus;

	@Inject
	private ResourceBundle messages;

	@Override
	public void run() {
		setLookAndFeel();
		setExceptionHandler();

		try {
			backup();
			frame = injector.getInstance(ArchivariumFrame.class);
			frame.launch();
		} catch (Exception e) {
			eventBus.fireEvent(new ExceptionEvent(Severity.ERROR, e
					.getMessage(), e));
		}
	}

	private void backup() throws SQLException {
		String backups = System.getProperty("archivarium.backup_dir");
		File database = new File(System.getProperty("archivarium.db"))
				.getAbsoluteFile();

		File older = null;
		File f = null;
		for (int i = 0; i < 10; i++) {
			f = new File(backups, "archivarium." + i + ".zip");
			if (!f.exists()) {
				break;
			}

			if (older == null || older.lastModified() > f.lastModified()) {
				older = f;
			}
		}

		f = f.exists() ? older : f;

		Backup.execute(f.getAbsolutePath(), database.getParent(),
				database.getName(), true);
	}

	private void setLookAndFeel() {
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
	}

	private void setExceptionHandler() {
		exceptionHandler = new ExceptionEventHandler() {
			@Override
			public void exception(Severity severity, String message, Throwable t) {
				int messageType;
				String title;
				switch (severity) {
				case ERROR:
					messageType = JOptionPane.ERROR_MESSAGE;
					title = "Error";
					logger.error(message, t);
					break;
				case WARNING:
					messageType = JOptionPane.WARNING_MESSAGE;
					title = "Warning";
					logger.warn(message, t);
					break;
				case INFO:
				default:
					messageType = JOptionPane.INFORMATION_MESSAGE;
					title = "Info";
					logger.info(message, t);
					break;
				}

				JOptionPane.showMessageDialog(null,
						messages.getString(message), messages.getString(title),
						messageType);
			}
		};

		eventBus.addHandler(ExceptionEvent.class, exceptionHandler);
	}
}
