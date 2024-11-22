package net.noahf.scanner.audio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Recorder {

    private final AudioListener listener;

    private Recording currentRecording;

    private final String[] headers;
    private int index;
    private List<String[]> audioDataWhileRecording;

    public Recorder(AudioListener listener) {
        this.listener = listener;
        this.currentRecording = null;

        this.headers = new String[]{"Frame", "TimeMs", "Frequency", "AvgFrequency", "Volume", "LastSoundUnix", "LastSoundAgoSeconds", "HasSound", "HasSoundRecently"};
        this.index = 0;
        this.audioDataWhileRecording = new ArrayList<>();
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

        // write data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(this.currentRecording.getFile().getParentFile(), "last-audio-file-data.csv")))) {
            writer.write(String.join(",", headers));
            writer.newLine();

            for (String[] row : this.audioDataWhileRecording) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        } catch (Exception exception) {
            System.err.println("Failed to write " + this.audioDataWhileRecording.size() + " frames of audio data: " + exception);
        }

        this.index = 0;
        this.audioDataWhileRecording = new ArrayList<>();

        this.currentRecording.end();
        this.currentRecording = null;
    }


    void tick(AudioFrame frame) {
        if (!this.isRecording()) return;

        String[] data = new String[this.headers.length];
        data[0] = String.valueOf(this.index);
        data[1] = String.valueOf((double) (System.currentTimeMillis() - this.currentRecording.getTimeStarted().getTime()) / 1000.0D);
        data[2] = String.valueOf(System.currentTimeMillis());
        data[3] = String.valueOf(frame.getFrequency());
        data[4] = String.valueOf(frame.getAverageFrequency());
        data[5] = String.valueOf(frame.getVolume());
        data[6] = String.valueOf(frame.getLastSoundDetectionTime());
        data[7] = String.valueOf((double) (System.currentTimeMillis() - frame.getLastSoundDetectionTime()) / 1000.0D);
        data[8] = String.valueOf(frame.hasSound());
        data[9] = String.valueOf(frame.hasSoundRecently());

        this.audioDataWhileRecording.add(data);
    }

}
