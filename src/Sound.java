import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.*;
import javax.swing.*;

public class Sound {

    private Clip clip;


    public volatile boolean running,looping;

    int currentFrame = 0;
    public Sound(File location){
        init(location);
    }

    public void init(File location) {
        looping = running = false;
        try {
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(location));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void play(WavPanel panel) {
        running = true;
        if(isResumed()) {
            clip.setFramePosition(currentFrame);
        }
        new Thread(new Runnable(){
            public void run(){
                System.out.println("Buffer:  "+clip.getBufferSize());
                System.out.println("Format:  "+clip.getFormat());
                clip.start();
                while(true){
                    panel.setCurrentIndex(clip.getFramePosition());
                    if(clip.getMicrosecondPosition() >= clip.getMicrosecondLength())
                        break;
                    if(!running)
                        break;
                }
            }
        }).start();
    }
    public void pause(WavPanel panel){
        running = false;
        clip.stop();
        currentFrame = clip.getFramePosition();
    }
    public void resume(WavPanel panel){
        running = true;
        new Thread(new Runnable(){
            public void run(){
                clip.setFramePosition(currentFrame);
                panel.setCurrentIndex(currentFrame);
                clip.start();
                while(true){
                    panel.setCurrentIndex(clip.getFramePosition());
                    panel.repaint();
                    if(clip.getMicrosecondPosition() == clip.getMicrosecondLength())
                        break;
                    if(!running)
                        break;
                }
            }
        }).start();
    }
    public void stop(WavPanel panel){
        running = false;
        looping = false;
        clip.stop();
        currentFrame = 0;
        clip.setFramePosition(0);
    }
    public boolean isResumed(){
        return currentFrame > 0;
    }
    public Clip getClip() {
        return clip;
    }

    public static double[] read(File filename) {
        byte[] data = readByte(filename);
        int N = data.length;
        double[] d = new double[N/2];
        for (int i = 0; i < N/2; i++) {
            d[i] = ((short) (((data[2*i+1] & 0xFF) << 8) + (data[2*i] & 0xFF))) / ((double) Short.MAX_VALUE);
        }
        return d;
    }

    // return data as a byte array
    private static byte[] readByte(File filename) {
        byte[] data = null;
        AudioInputStream ais = null;
        try {
            ais = AudioSystem.getAudioInputStream(filename);
            data = new byte[ais.available()];
            ais.read(data);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Could not read " + filename);
        }

        return data;
    }

}