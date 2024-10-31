package net.noahf.scanner.scanner;

import net.noahf.scanner.Main;
import net.noahf.scanner.audio.AudioFrame;
import net.noahf.scanner.audio.AudioListener;
import org.jtransforms.fft.DoubleFFT_1D;

public class ScannerInterpreter {

    private final AudioListener listener;

    public ScannerInterpreter(AudioListener listener) {
        this.listener = listener;

        this.listener.setTick((frame) -> {
            DoubleFFT_1D fft = new DoubleFFT_1D(frame.getSamples().length);

            double frequency = getDominantFrequency(frame, fft);

//            System.out.println("Frequency: " + frequency + " Hz");
        });
    }

    /**
     * Fourier Transform
     */
    public double getDominantFrequency(AudioFrame frame, DoubleFFT_1D fft) {
        double[] samples = frame.getSamples();
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

}
