package id.ac.itb.lumen.sinsymaker;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.WaveformWriter;
import com.sun.media.sound.AudioFloatInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.nio.FloatBuffer;

@SpringBootApplication
@Profile("resaveaudio")
public class ResaveAudioApp implements CommandLineRunner {

    private static Logger log = LoggerFactory.getLogger(ResaveAudioApp.class);

    public static void main(String[] args) {
        new SpringApplicationBuilder(ResaveAudioApp.class)
                .profiles("resaveaudio")
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        final File sourceAudioFile = new File("/together/project_amanah/lumen/speech/expressive/dongeng-bangau.wav");
        final File cloneAudioFile = new File("/together/project_amanah/lumen/speech/expressive/dongeng-bangau-clone.wav");
        log.info("Cloning {} to {} ...", sourceAudioFile, cloneAudioFile);

        // be careful with block size, since TersosDSP does "overshoot" samples
        final AudioDispatcher dispatcher = AudioDispatcherFactory.fromFile(sourceAudioFile, 2 * 1024, 0);
        final TarsosDSPAudioFormat stdFormat = new TarsosDSPAudioFormat(44100, 16, 1, true, false);
        dispatcher.addAudioProcessor(new WaveformWriter(stdFormat, cloneAudioFile.getPath()));
        dispatcher.run();
    }
}
