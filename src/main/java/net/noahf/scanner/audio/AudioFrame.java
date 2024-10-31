package net.noahf.scanner.audio;

import net.noahf.scanner.Main;
import org.jtransforms.fft.DoubleFFT_1D;

import javax.sound.sampled.TargetDataLine;

public class AudioFrame {

    private final byte[] buffer = new byte[Main.BUFFER_SIZE];
    private final double[] samples = new double[Main.BUFFER_SIZE / 2];
    private final int bytesRead;

    AudioFrame(AudioListener listener) {
        TargetDataLine data = listener.getMicrophone().getDataLine();
        this.bytesRead = data.read(buffer, 0, buffer.length);
        for (int i = 0, j = 0; i < bytesRead - 1; i += 2, j++) {
            samples[j] = ((buffer[i + 1] << 8) | (buffer[i] & 0xFF)) / 32768.0;
        }
    }

    public int getBytesRead() { return this.bytesRead; }

    public byte[] getBuffer() { return this.buffer; }

    public double[] getSamples() { return this.samples; }

}
