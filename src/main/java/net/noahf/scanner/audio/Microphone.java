package net.noahf.scanner.audio;

import net.noahf.scanner.Main;

import javax.sound.sampled.*;
import java.sql.SQLOutput;

public class Microphone {

    public static int USE_MICROPHONE = -1;

    private final AudioFormat format;
    private final TargetDataLine microphone;
    private final String selectedMicrophone;

    public Microphone(AudioFormat format) throws LineUnavailableException {
        this.format = format;

        System.out.println("Detecting available microphones...");
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        for (int i = 0; i < mixers.length; i++) {
            Mixer.Info mixer = mixers[i];
            System.out.println("Microphone #" + i + ": " + mixer.getName() + " - " + mixer.getDescription());
        }

        if (USE_MICROPHONE == -1) { // use default
            this.selectedMicrophone = "default";
            this.microphone = AudioSystem.getTargetDataLine(format);
        } else {
            Mixer.Info selectedMixer = mixers[USE_MICROPHONE];
            this.selectedMicrophone = selectedMixer.getName();
            Mixer mixer = AudioSystem.getMixer(selectedMixer);
            this.microphone = (TargetDataLine) mixer.getLine(new DataLine.Info(TargetDataLine.class, format));
        }
        System.out.println("Selected Microphone: " + this.getSelectedMicrophone());

        this.microphone.open(format, Main.BUFFER_SIZE);
        this.microphone.start();
    }

    public TargetDataLine getDataLine() {
        return this.microphone;
    }

    public String getSelectedMicrophone() {
        return this.selectedMicrophone;
    }

}
