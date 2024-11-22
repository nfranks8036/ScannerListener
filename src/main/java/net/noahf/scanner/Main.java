package net.noahf.scanner;

import net.noahf.scanner.audio.AudioListener;

public class Main {

    public static final int BUFFER_SIZE = 16384; // for ftt analysis

    public static void main(String[] args) {
        try {
            AudioListener audioListener = new AudioListener();

            audioListener.setTick((frame) -> {
//                System.out.println("SOUND DETECTED: " + frame.getVolume() + " dB, " + frame.getFrequency() + " MHz, " + (System.currentTimeMillis() - frame.getLastSoundDetectionTime()) + "ms");
                if (!frame.hasSoundRecently()) {
                    if (frame.getRecorder().isRecording()) {
                        frame.getRecorder().stopRecording();
                    }
                    return;
                }

                if (!frame.getRecorder().isRecording()) {
                    frame.getRecorder().startRecording();
                }
            });
        } catch (Exception exception) {
            throw new RuntimeException("ScannerListener failed to execute: " + exception, exception);
        }
    }

}