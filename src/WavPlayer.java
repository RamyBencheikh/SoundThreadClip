import java.awt.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.swing.*;
import java.io.*;
import javax.swing.filechooser.FileFilter;

public class WavPlayer extends JFrame {
    private JFileChooser chooser;
	private Toolbar toolbar;
    private File audioFile;

	private Sound player;

	private WavPanel panel;

	private Clip audioClip;

	private String pathFile;

	private boolean isPause = false;
    public WavPlayer() {
		super("WavPlayerClip");

		this.chooser = new JFileChooser();

		this.chooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return null;
			}

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".wav");
			}
		});

		int returnVal = chooser.showOpenDialog(this);
		if((returnVal == JFileChooser.APPROVE_OPTION) && (chooser.getSelectedFile().getName().contains(".wav"))) {
			this.audioFile = chooser.getSelectedFile();
		}

		this.setSize(1000, 800);

		this.setLayout(new BorderLayout());
        this.toolbar = new Toolbar();
		this.loadAudio();
		this.panel.setPreferredSize(new Dimension(500, 150));
		this.audioClip = player.getClip();
        this.toolbar.setStringListener(new StringListener() {
            @Override
            public void textEmitted(String text) {
                switch (text) {
                    case "Play":
						isPause = false;
						player.play(panel);
                        break;
                    case "Pause":
						isPause = true;
						player.pause(panel);
                        break;
                    case "Stop":
						isPause = false;
						player.stop(panel);
                        break;
                    case "Resume":
						isPause = false;
						player.resume(panel);
						break;
                    default:
						setFile();
                        break;
                }
            }
        });

		// Load the audio to get the waveform
		this.panel.load(audioFile);

        // Adds components(child's) into the Layout
        this.add(toolbar, BorderLayout.NORTH);
        this.add(panel, BorderLayout.CENTER);
		this.createListener(this.audioClip);

		// Adapt the windows with all size of component
		this.pack();

        // Close the program when close the windows
        this.setDefaultLookAndFeelDecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

	public void loadAudio() {
		this.player = new Sound(audioFile);
		this.panel = new WavPanel();
		panel.setClipLenght(player.getClip().getFrameLength());
	}

	protected void setFile() {
		this.audioClip.stop();
		int returnVal = chooser.showOpenDialog(this);
		if((returnVal == JFileChooser.APPROVE_OPTION) && (chooser.getSelectedFile().getName().contains(".wav"))) {
			this.audioFile = chooser.getSelectedFile();
			this.panel.load(audioFile);
			this.player.init(audioFile);
			this.audioClip = this.player.getClip();
			panel.setClipLenght(player.getClip().getFrameLength());
			this.createListener(this.audioClip);
		}
	}

	public void createListener(Clip clip) {
		clip.addLineListener(event -> {
			if (event.getType() == LineEvent.Type.STOP) {
				if(!isPause){
					player.currentFrame = 0;
					player.stop(panel);
					this.panel.setCurrentIndex(0);
					this.panel.repaint();
				};
			} else if (event.getType() == LineEvent.Type.START) {
				while(audioClip.isRunning()) {
					this.panel.repaint();
				}
			}
		});
	}
}
