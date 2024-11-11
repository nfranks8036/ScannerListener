package net.noahf.scanner.scanner;

import net.noahf.scanner.Main;
import net.noahf.scanner.audio.AudioFrame;
import net.noahf.scanner.audio.AudioListener;
import org.jtransforms.fft.DoubleFFT_1D;

public class ScannerInterpreter {

    private final AudioListener listener;

    public ScannerInterpreter(AudioListener listener) throws InterruptedException {
        this.listener = listener;

        this.listener.setTick((frame) -> {
            if (frame.getFrequency() == 0.0D) return;

            System.out.println("\n\n\n\n"
                    + "Frequency: " + frame.getFrequency() + " Hz\n"
                    + "Smoothed Frequency: " + frame.getSmoothedFrequency() + " Hz\n"
                    + "Has frequency? " + frame.hasRecentFrequency(18244, 100)
            );
        });
    }

}
