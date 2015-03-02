package id.ac.itb.lumen.sinsymaker;

import org.JMathStudio.Exceptions.UnSupportedAudioFormatException;
import org.JMathStudio.Interface.AudioInterface.AudioBuffer;
import org.JMathStudio.Interface.AudioInterface.AudioDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.SizeLimitExceededException;
import java.io.File;
import java.io.IOException;

/**
 * Created by ceefour on 02/03/15.
 */
public class AudioReader {

    private static Logger log = LoggerFactory.getLogger(AudioReader.class);

    public AudioBuffer read(File file) {
        final AudioDecoder decoder = new AudioDecoder();
        try {
            log.info("Reading audio '{}' ...", file);
            final AudioBuffer buffer = decoder.decodeAudioData(file.getPath());
            log.info("bufferSize={} encoding={} channels={} sampleRate={} sampleSize={}",
                    buffer.accessAudioBuffer().size(), buffer.accessAudioFormat().getEncoding(),
                    buffer.accessAudioFormat().getChannels(), buffer.accessAudioFormat().getSampleRate(),
                    buffer.accessAudioFormat().getSampleSizeInBits());
            return buffer;
        } catch (Exception e) {
            throw new RuntimeException("Cannot read audio file " + file, e);
        }
    }
}
