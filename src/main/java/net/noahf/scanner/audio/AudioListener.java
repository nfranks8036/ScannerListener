package net.noahf.scanner.audio;

import net.noahf.scanner.recording.Recorder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import java.util.function.Consumer;

public class AudioListener {

    public static final float SAMPLE_RATE = 44_100.0f; // cd quality

    private final AudioFormat format;
    private final Microphone microphone;
    private final Recorder recorder;

    private Thread thread;
    private Consumer<AudioFrame> tick;

    double[] frequencyHistory = new double[10];
    int frequencyHistoryIndex = 0;

    public AudioListener() throws LineUnavailableException {
        this.format = new AudioFormat(SAMPLE_RATE, 16, 1, true, true);
        this.microphone = new Microphone(this.format);
        this.recorder = new Recorder(this);
    }

    public AudioFormat getAudioFormat() { return this.format; }
    public Microphone getMicrophone() { return this.microphone; }
    public Thread getListenerThread() { return this.thread; }
    public Recorder getRecorder() { return this.recorder; }

    public void setTick(Consumer<AudioFrame> tick) throws InterruptedException {
        this.tick = tick;

        this.thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    this.tick.accept(new AudioFrame(this));
                } catch (Exception exception) {
                    System.out.println("An error occurred with the audio listener: " + exception);
                    Thread.currentThread().interrupt();
                }
            }
        }, "ScannerListener::AudioListener");
        this.thread.start();
        this.thread.join();
    }

}