package net.noahf.scanner.audio;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("CallToPrintStackTrace")
public class Recording {

    private static final DateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private static final DateFormat LOG_DATE_FORMAT = new SimpleDateFormat("MMM d, yyyy 'at' HH:mm:ss");

    private final Thread thread;
    private final AudioListener listener;

    private Date started;
    private File file;
    private AudioInputStream audioInputStream;
    private TargetDataLine microphone;

    private double[][] soundData;

    public Recording(AudioListener listener) {
        this.listener = listener;
        this.thread = new Thread(this::record);
        this.thread.start();
    }

    private void record() { // all in its own thread
        try {
            this.started = new Date();
            this.file = new File("recordings/toned_" + FILE_DATE_FORMAT.format(this.started) + ".wav");
            if (!this.file.getParentFile().exists() && !this.file.getParentFile().mkdirs() ) {
                throw new IllegalStateException("Failed to create path for " + this.file.getAbsolutePath());
            }

            System.out.println("Started a recording at " + LOG_DATE_FORMAT.format(new Date()));

            this.microphone = this.listener.getMicrophone().getDataLine();
            if (!this.microphone.isActive()) {
                this.microphone.start();
            }
            this.microphone.flush();

            this.audioInputStream = new AudioInputStream(microphone);

            AudioSystem.write(this.audioInputStream, AudioFileFormat.Type.WAVE, this.file);
        } catch (Exception exception) {
            exception.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public void end() {
        try {
            this.microphone.stop();
            this.microphone.flush();

            this.thread.interrupt();

            this.microphone.start();

            System.out.println("Ended a recording at " + LOG_DATE_FORMAT.format(new Date()) + ", lasting " +
                    (((double)System.currentTimeMillis() - (double)this.started.getTime()) / 1000D) + "s"
            );
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to end and save recording of " + file.getAbsolutePath() + ": " + exception, exception);
        }
    }

    public File getFile() { return this.file; }
    public Date getTimeStarted() { return this.started; }

}
