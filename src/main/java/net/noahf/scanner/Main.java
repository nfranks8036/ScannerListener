package net.noahf.scanner;

import net.noahf.scanner.audio.AudioListener;

public class Main {

    public static final int BUFFER_SIZE = 16384; // for ftt analysis

    public static void main(String[] args) {
        try {
            AudioListener audioListener = new AudioListener();

            audioListener.setTick((frame) -> {
                if (frame.getFrequency() <= 100.000D) { // likely just noise
                    if (frame.getRecorder().isRecording()) {
                        frame.getRecorder().stopRecording();
                    }
                    return;
                }

                System.out.println("Is recording: " + frame.getRecorder().isRecording());

                if (!frame.getRecorder().isRecording()) {
                    frame.getRecorder().startRecording();
                }

//            System.out.println("\n\n\n\n"
//                    + "Frequency: " + frame.getFrequency() + " Hz\n"
//                    + "Smoothed Frequency: " + frame.getAverageFrequency() + " Hz\n"
//                    + "Has frequency? " + frame.hasRecentFrequency(18244, 100)
//            );
            });
        } catch (Exception exception) {
            throw new RuntimeException("ScannerListener failed to execute: " + exception, exception);
        }
    }

}