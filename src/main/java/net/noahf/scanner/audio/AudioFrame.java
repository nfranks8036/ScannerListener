package net.noahf.scanner.audio;

import net.noahf.scanner.Main;
import org.jtransforms.fft.DoubleFFT_1D;

import javax.sound.sampled.TargetDataLine;
import java.util.Arrays;

public class AudioFrame {

    private final AudioListener listener;

    private final byte[] buffer;
    private final double[] samples;
    private final int bytesRead;
    private final double frequency;
    private final double volume;
    private final double averageFrequency;

    AudioFrame(AudioListener listener) {
        this.listener = listener;
        this.buffer = new byte[Main.BUFFER_SIZE];
        this.samples = new double[Main.BUFFER_SIZE / 2];

        TargetDataLine data = listener.getMicrophone().getDataLine();
        this.bytesRead = data.read(buffer, 0, buffer.length);
        for (int i = 0, j = 0; i < bytesRead - 1; i += 2, j++) {
            samples[j] = ((buffer[i + 1] << 8) | (buffer[i] & 0xFF)) / 32768.0;
        }

//        this.applyHammingWindow();

        this.frequency = this.applyIgnoredFrequencies(this.getDominantFrequency());
        this.volume = this.getCalculatedVolume();

        listener.frequencyHistory[listener.frequencyHistoryIndex] = this.frequency;
        listener.frequencyHistoryIndex = (listener.frequencyHistoryIndex + 1) % listener.frequencyHistory.length;
        this.averageFrequency = Arrays.stream(listener.frequencyHistory).average().orElse(this.frequency);

        listener.lastHadSound = (this.hasSound() ? System.currentTimeMillis() : listener.lastHadSound);
    }

    public int getBytesRead() { return this.bytesRead; }

    public byte[] getBuffer() { return this.buffer; }

    public double[] getSamples() { return this.samples; }

    public double getVolume() { return this.volume; }

    public double getFrequency() { return this.frequency; }

    public double getAverageFrequency() { return this.averageFrequency; }

    public Recorder getRecorder() { return this.listener.getRecorder(); }

    public long getLastSoundDetectionTime() { return this.listener.lastHadSound; }



    public boolean hasSoundRecently() {
        return this.hasSound()
                || (System.currentTimeMillis() - this.getLastSoundDetectionTime() < 2000D);
    }

    public boolean hasSound() {
        return this.getVolume() > 0.0D;
    }

    public boolean hasRecentFrequency(double frequency, int tolerance) {
        double[] frequencyHistory = this.listener.frequencyHistory;
        for (double loopFreq : frequencyHistory) {
            if (frequency == loopFreq) return true;
            if (Math.abs(loopFreq - frequency) <= tolerance) return true;
        }
        return false;
    }


    /**
     * Fourier Transform
     */
    private double getDominantFrequency() {
        double[] samples = this.getSamples();
        DoubleFFT_1D fft = new DoubleFFT_1D(samples.length);
        double[] fftData = new double[samples.length * 2];
        System.arraycopy(samples, 0, fftData, 0, samples.length);

        fft.realForwardFull(fftData);

        double maxMagnitude = -1;
        int dominantIndex = -1;

        for (int i = 0;  i < fftData.length / 2; i += 2) {
            double real = fftData[i];
            double imaginary = fftData[i + 1];
            double magnitude = Math.sqrt(real * real + imaginary * imaginary); // pythagorean theorem!

            if (magnitude > maxMagnitude) {
                maxMagnitude = magnitude;
                dominantIndex = i / 2;
            }
        }

        return (double) dominantIndex * AudioListener.SAMPLE_RATE / samples.length;
    }

    private double getCalculatedVolume() {
        long sum = 0;

        for (int i = 0; i < bytesRead; i += 2) {
            short sample = (short) (buffer[i + 1] << 8 | (buffer[i] & 0xFF));
            sum += (long) sample * sample;
        }

        double rms = Math.sqrt((double) sum / ((double) this.getBuffer().length / 2));
        double volume = rms / Short.MAX_VALUE;

        if (volume <= 0.05) { // negligible volume
            return 0.0F;
        }

        return volume;
    }

    private static final double[] IGNORED_FREQUENCIES = new double[]{59.21630859375D};

    private double applyIgnoredFrequencies(double frequency) {
        if (Arrays.stream(IGNORED_FREQUENCIES).anyMatch(f -> f == frequency))
            return 0.0D;
        return frequency;
    }

//    private void applyHammingWindow() {
//        int length = samples.length;
//        for (int i = 0; i < length; i++) {
//            samples[i] *= 0.54 - 0.46 * Math.cos(2 * Math.PI * i / (length - 1));
//        }
//    }

}
