package id.ac.itb.lumen.sinsymaker;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.io.jvm.WaveformWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.util.Random;

/**
 * Created by ceefour on 02/03/15.
 */
public class SegmentWaveformWriter implements AudioProcessor {
    private final AudioFormat format;
    private final File rawOutputFile;
    private final String fileName;
    private final long frameStart;
    private final long frameEnd;
    private final int attack;
    private final int decay;
    private BufferedOutputStream rawOutputStream;

    /**
     * Log messages.
     */
    private static final Logger LOG = LoggerFactory.getLogger(WaveformWriter.class);

    /**
     * The overlap and step size defined not in samples but in bytes. So it
     * depends on the bit depth. Since the integer data type is used only
     * 8,16,24,... bits or 1,2,3,... bytes are supported.
     */
    private int byteOverlap, byteStepSize;

    /**
     * Initialize the writer.
     * @param format The format of the received bytes.
     * @param fileName The name of the wav file to store.
     */
    public SegmentWaveformWriter(final AudioFormat format, final String fileName, long frameStart, long frameEnd,
                                 int attackFrames, int decayFrames){
        this.format = format;
        this.frameStart = frameStart;
        this.frameEnd = frameEnd;
        this.attack = attackFrames;
        this.decay = decayFrames;
        this.fileName = fileName;

        LOG.info("Streaming {} at {}..{} using {} attack={}frames decay={}frames",
                fileName, frameStart, frameEnd, format, attackFrames, decayFrames);

        try {
            //a temporary raw file with a random prefix
            this.rawOutputFile = File.createTempFile("out_", ".raw");
            try {
                this.rawOutputStream = new BufferedOutputStream(new FileOutputStream(rawOutputFile));
            } catch (FileNotFoundException e) {
                //It should always be possible to write to a temporary file.
                String message;
                message = String.format("Could not write to the temporary RAW file %1s: %2s", rawOutputFile.getAbsolutePath(), e.getMessage());
                LOG.error(message);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot determine temporary file name", e);
        }
    }

    public SegmentWaveformWriter(final TarsosDSPAudioFormat format, final String fileName, long frameStart, long frameEnd,
                                 int attack, int decay) {
        this(JVMAudioInputStream.toAudioFormat(format), fileName, frameStart, frameEnd, attack, decay);
    }

    public long getFrameStart() {
        return frameStart;
    }

    public long getFrameEnd() {
        return frameEnd;
    }

    public int getAttack() {
        return attack;
    }

    public int getDecay() {
        return decay;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        long startOffset = frameStart - audioEvent.getSamplesProcessed();
        long endOffset = frameEnd - audioEvent.getSamplesProcessed();
        int frameLength = audioEvent.getBufferSize(); // FIXME: bufferSize -> frameLength
        if (startOffset < frameLength && endOffset > 0) {
            this.byteOverlap = audioEvent.getOverlap() * format.getFrameSize();
            int byteStartOffset = startOffset >= 0 ? (int) (byteOverlap + startOffset * format.getFrameSize()) : byteOverlap;
            this.byteStepSize = frameLength * format.getFrameSize() - byteStartOffset;
            int byteEndOffset = endOffset < frameLength ? (int) (endOffset * format.getFrameSize() - byteStartOffset) : byteStepSize;
            try {
                rawOutputStream.write(audioEvent.getByteBuffer(), byteStartOffset, byteEndOffset);
            } catch (IOException e) {
                LOG.error(String.format("Failure while writing temporary file: %1s: %2s", rawOutputFile.getAbsolutePath(), e.getMessage()));
            }
        }
        return true;
    }

    @Override
    public void processingFinished() {
        File out = new File(fileName);
        try {
            try {
                //stream the raw file
                final BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(rawOutputFile));
                try {
                    long lengthInSamples = rawOutputFile.length() / format.getFrameSize();
                    LOG.info("Writing {} from {} ({} samples)", fileName, rawOutputFile, lengthInSamples);
                    final AudioInputStream audioInputStream;
                    //create an audio stream form the raw file in the specified format
                    audioInputStream = new AudioInputStream(inputStream, format, lengthInSamples);
                    try {
                        //stream this to the out file
                        final BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(out));
                        try {
                            //stream all the bytes to the output stream
                            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, fos);
                        } finally {
                            //cleanup
                            fos.close();
                        }
                    } finally {
                        audioInputStream.close();
                    }
                } finally {
                    inputStream.close();
                }
            } finally {
                rawOutputStream.close();
                rawOutputFile.delete();
            }
        } catch (IOException e) {
            String message;
            message = String.format("Error writing the WAV file %1s: %2s", out.getAbsolutePath(), e.getMessage());
            LOG.error(message);
        }
    }
}
