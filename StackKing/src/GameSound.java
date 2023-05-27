import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class GameSound {

    private Clip clip;
    public boolean gameOver = false;

    public GameSound() {

    }

    public void playSound(String filePath, float volume, boolean loop) {
        try {
            // Load the audio file
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource(filePath));

            // Get a clip for playing the audio
            clip = AudioSystem.getClip();

            // Open the audio clip with the loaded audio stream
            clip.open(audioInputStream);

            // Get the gain control for adjusting the volume
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            // Calculate the desired gain value based on the volume level
            float gain = volumeToGain(volume);

            // Set the gain value
            gainControl.setValue(gain);

            // Start playing the audio clip
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopSound() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }

    private float volumeToGain(float volume) {
        // Calculate the gain value based on the volume level
        if (volume <= 0.0f) {
            return 0.0f;
        } else if (volume >= 1.0f) {
            return 1.0f;
        } else {
            return (float) (Math.log10(volume) * 20.0);
        }
    }

}
