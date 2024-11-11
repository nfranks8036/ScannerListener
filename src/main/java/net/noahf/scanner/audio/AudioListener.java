package net.noahf.scanner.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.noahf.scanner.Main.BUFFER_SIZE;

public class AudioListener {

    public static final int SAMPLE_RATE = 44_100; // cd quality

    private final AudioFormat format;
    private final Microphone microphone;

    private Thread thread;
    private Consumer<AudioFrame> tick;

    double[] frequencyHistory = new double[10];
    int frequencyHistoryIndex = 0;

    public AudioListener() throws LineUnavailableException {
        this.format = new AudioFormat(SAMPLE_RATE, 16, 1, true, true);
        this.microphone = new Microphone(this.format);
    }

    public AudioFormat getAudioFormat() { return this.format; }
    public Microphone getMicrophone() { return this.microphone; }
    public Thread getListenerThread() { return this.thread; }

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