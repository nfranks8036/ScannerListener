package net.noahf.scanner.recording;

import net.noahf.scanner.audio.AudioListener;

public class Recorder {

    private final AudioListener listener;

    private Recording currentRecording;

    public Recorder(AudioListener listener) {
        this.listener = listener;
        this.currentRecording = null;
    }

    public boolean isRecording() {
        return this.currentRecording != null;
    }

    public void startRecording() {
        if (this.isRecording()) throw new IllegalStateException("Already recording.");
        this.currentRecording = new Recording(this.listener);
    }

    public void stopRecording() {
        if (!this.isRecording()) throw new IllegalStateException("Not currently recording.");
        this.currentRecording.end();
        this.currentRecording = null;
    }

}
