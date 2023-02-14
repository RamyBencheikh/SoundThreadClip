import java.awt.*;
import java.io.File;
import javax.swing.*;


public class WavPanel extends JPanel 
{
	public WavPanel() {
		this.setPreferredSize(new Dimension(500, 150));
	}
	// Holds the currently loaded audio data
	private double [] audio = null;
	private int clipLenght = 1;

	// Present playback position, -1 if not currently playing
	private int currentIndex = -1;

	public void setClipLenght(int i) {
		this.clipLenght = i;
	}

	// Attempts to load the audio in the given filename.
	// Returns true on success, false otherwise.
	public boolean load(File filename) {
		audio = Sound.read(filename);
		if(audio.length == 0) {
			audio = null;
			return false;
		}else {
			currentIndex = 0;
			repaint();
			return true;
		}
	}

	// Get the index of the next audio sample that will be played.
	// Returns -1 if playback isn't active.
	public int getCurrentIndex() {
		return currentIndex;
	}

	// Sets the index of the current audio sample.
	// Client may set to -1 when playback is not active (no red line drawn).
	// Client may set to 0 when playback is about to start.
	public void setCurrentIndex(int i) {
		currentIndex = i;
	}

	// Draw the waveform and the current playing position (if any)
	// This method shouldn't be called directly.  The player
	// should call repaint() whenever you need to update the
	// waveform visualization.-
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		drawWaveform(g2d);
		drawCursor(g2d);
	}

	private void drawWaveform(Graphics2D g2d) {
		if (audio != null) {
			for (int i = 0; i < audio.length; i++) {
				int x = i * getWidth() / audio.length;
				double toDrawVal = Math.abs(audio[i]);
				double lengthToDraw = getHeight() * toDrawVal;
				int yMin = (int)(getHeight() - lengthToDraw) / 2;
				int yMax = (int)(yMin + lengthToDraw);
				g2d.setColor(Color.BLUE);
				g2d.drawLine(x, yMin, x, yMax);
			}
		}
	}

	private void drawCursor(Graphics2D g2d) {
		if (audio != null) {
			int x = currentIndex * getWidth() / clipLenght;
			g2d.setColor(Color.RED);
			g2d.drawLine(x, 0, x, getHeight());
		}
	}

	public void update() {
		repaint();
	}

}
