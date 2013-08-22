package ch.arrg.logreader.preprocessor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.Timer;

import ch.arrg.logreader.core.Config;

public class Beeper implements AbstractPreProcessor {
	private final String[] BEEP_PATTERNS = Config.getStringArrayProp("preproc.beeper.patterns");

	private final boolean ENABLE_AUDIO = Config.getBoolProp("preproc.beeper.enableAudio");
	private final boolean ENABLE_VIDEO = Config.getBoolProp("preproc.beeper.enableVideo");

	private final int NOTIF_DURATION = Config.getIntProp("preproc.beeper.duration");
	private final int NOTIF_SIZE = Config.getIntProp("preproc.beeper.font.size");

	@Override
	public String process(String line) {
		for (String pattern : BEEP_PATTERNS) {
			if (line.contains(pattern)) {
				notify(line);
				break;
			}
		}
		return line;
	}

	@Override
	public void reset() {

	}

	private void notify(String line) {
		if (ENABLE_AUDIO) {
			Toolkit.getDefaultToolkit().beep();
		}

		if (ENABLE_VIDEO) {
			final NotifWindow w = new NotifWindow(line);
			w.display();
		}
	}

	class NotifWindow extends JWindow {
		private JLabel label;

		public NotifWindow(String text) {

			label = new JLabel(text);
			Font font = new Font("Consolas", Font.BOLD, NOTIF_SIZE);
			label.setFont(font);
			add(label);

			setBackground(new Color(0, 0, 0, 0));

			setAlwaysOnTop(true);
			setLocation(10, 10);

			validate();
			pack();
		}

		public void display() {
			setVisible(true);

			final long timerEnd = System.currentTimeMillis() + NOTIF_DURATION;

			Timer timer = new Timer(20, new ActionListener() {
				float hue = 0;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (System.currentTimeMillis() > timerEnd) {
						dispose();
					} else {
						Color now = Color.getHSBColor(hue, 1f, 1f);
						label.setForeground(now);
						hue += 0.02;
					}
				}
			});

			timer.start();
		}
	}

}
