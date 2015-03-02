package id.ac.itb.lumen.sinsymaker;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import com.google.common.util.concurrent.SettableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.FloatBuffer;

/**
 * Created by ceefour on 02/03/15.
 */
public class AudioReader {

    private static Logger log = LoggerFactory.getLogger(AudioReader.class);

    public FloatBuffer read(File file) {
        try {
            log.info("Reading audio '{}' ...", file);
            final int BUFFER_SIZE = 200 * 1024 * 1024; // typical PCM WAV is < 200 MB, up to max of 2 GB
            final FloatBuffer buffer = FloatBuffer.allocate(BUFFER_SIZE);
            final AudioDispatcher dispatcher = AudioDispatcherFactory.fromFile(file, 10 * 1024 * 1024, 0); // chunks
            dispatcher.addAudioProcessor(new AudioProcessor() {
                @Override
                public boolean process(AudioEvent audioEvent) {
                    log.info("AudioEvent bufferSize={} timeStamp={} RMS={} samplesProcessed={} sampleRate={}",
                            audioEvent.getBufferSize(), audioEvent.getTimeStamp(), audioEvent.getRMS(),
                            audioEvent.getSamplesProcessed(),
                            audioEvent.getSampleRate());
                    buffer.put(audioEvent.getFloatBuffer());
                    audioEvent.clearFloatBuffer();
                    return true;
                }

                @Override
                public void processingFinished() {
                    log.info("Finished at {}", buffer.position());
                    buffer.limit(buffer.position());
                }
            });
            dispatcher.run();
            log.info("Done, floatBuffer.limit={} capacity={}", buffer.limit(), buffer.capacity());
            return buffer;
        } catch (Exception e) {
            throw new RuntimeException("Cannot read audio file " + file, e);
        }
    }
}
