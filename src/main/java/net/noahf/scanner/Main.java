package net.noahf.scanner;

import net.noahf.scanner.audio.AudioListener;
import net.noahf.scanner.scanner.ScannerInterpreter;

public class Main {

    public static final int BUFFER_SIZE = 4096; // for ftt analysis

    public static void main(String[] args) {
        try {
            AudioListener audioListener = new AudioListener();

            ScannerInterpreter interpreter = new ScannerInterpreter(audioListener);

            while (true) {
                // do nothing
            }
        } catch (Exception exception) {
            throw new RuntimeException("ScannerListener failed to execute: " + exception, exception);
        }
    }

}