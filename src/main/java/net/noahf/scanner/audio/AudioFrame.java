package net.noahf.scanner.audio;

import net.noahf.scanner.Main;
import org.jtransforms.fft.DoubleFFT_1D;

import javax.sound.sampled.TargetDataLine;
import java.util.Arrays;

public class AudioFrame {

    private final AudioListener listener;

    private final byte[] buffer = new byte[Main.BUFFER_SIZE];
    private final double[] samples = new double[Main.BUFFER_SIZE / 2];
    private final int bytesRead;
    private final double frequency;
    private final double smoothedFrequency;

    AudioFrame(AudioListener listener) {
        this.listener = listener;

        TargetDataLine data = listener.getMicrophone().getDataLine();
        this.bytesRead = data.read(buffer, 0, buffer.length);
        for (int i = 0, j = 0; i < bytesRead - 1; i += 2, j++) {
            samples[j] = ((buffer[i + 1] << 8) | (buffer[i] & 0xFF)) / 32768.0;
        }

        this.applyHammingWindow();

        this.frequency = this.getDominantFrequency();

        listener.frequencyHistory[listener.frequencyHistoryIndex] = this.frequency;
        listener.frequencyHistoryIndex = (listener.frequencyHistoryIndex + 1) % listener.frequencyHistory.length;
        this.smoothedFrequency = Arrays.stream(listener.frequencyHistory).average().orElse(this.frequency);
    }

    public int getBytesRead() { return this.bytesRead; }

    public byte[] getBuffer() { return this.buffer; }

    public double[] getSamples() { return this.samples; }

    public double getFrequency() { return this.frequency; }

    public double getSmoothedFrequency() { return this.smoothedFrequency; }




    public boolean hasRecentFrequency(double frequency, int buffer) {
        double[] frequencyHistory = this.listener.frequencyHistory;
        for (double loopFreq : frequencyHistory) {
            if (frequency == loopFreq) return true;
            if (loopFreq < frequency + buffer && loopFreq > frequency - buffer) return true;
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
            double magnitude = Math.sqrt(real * real + imaginary * imaginary); // pythagorean therom!

            if (magnitude > maxMagnitude) {
                maxMagnitude = magnitude;
                dominantIndex = i / 2;
            }
        }

        return (double) dominantIndex * AudioListener.SAMPLE_RATE / samples.length;
    }

    private void applyHammingWindow() {
        int length = samples.length;
        for (int i = 0; i < length; i++) {
            samples[i] *= 0.54 - 0.46 * Math.cos(2 * Math.PI * i / (length - 1));
        }
    }

}
