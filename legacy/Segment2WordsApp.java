package id.ac.itb.lumen.sinsymaker;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import com.google.common.base.Splitter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.util.List;

@SpringBootApplication
@Profile("segment2words")
public class Segment2WordsApp implements CommandLineRunner {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Segment2WordsApp.class)
                .profiles("segment2words")
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        final TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(44100, 16, 1, true, false);
        final File clauseAudioFile = new File("/together/project_amanah/lumen/speech/expressive/clauses/perangkap oleh.wav");
        final File targetDir = new File("/together/project_amanah/lumen/speech/expressive/words");
        targetDir.mkdirs();

        final String clause = FilenameUtils.getBaseName(clauseAudioFile.getName());
        final List<String> splitClause = Splitter.on(' ').splitToList(clause);
        double estMedianPos = splitClause.get(0).length() * 1d / (splitClause.get(0).length() + splitClause.get(1).length());

        final AudioDispatcher dispatcher = AudioDispatcherFactory.fromFile(clauseAudioFile, 2048, 0);
        long estMedianFrame = Math.round(estMedianPos * dispatcher.durationInFrames());
        final int ATTACK_MS = 500;
        final int RELEASE_MS = 500;
        final int attackFrames = Math.round(ATTACK_MS / 1000f * format.getSampleRate());
        final int releaseFrames = Math.round(RELEASE_MS / 1000f * format.getSampleRate());
        // with attack + release
        dispatcher.addAudioProcessor(new SegmentWaveformWriter(format, new File(targetDir, splitClause.get(0) + ".wav").getPath(),
            0, estMedianFrame + releaseFrames, attackFrames, releaseFrames));
        dispatcher.addAudioProcessor(new SegmentWaveformWriter(format, new File(targetDir, splitClause.get(1) + ".wav").getPath(),
            estMedianFrame - attackFrames, dispatcher.durationInFrames(), attackFrames, releaseFrames));
        // strict splitter
//        dispatcher.addAudioProcessor(new SegmentWaveformWriter(format, new File(targetDir, splitClause.get(0) + ".wav").getPath(),
//            attackFrames, estMedianFrame, 0, 0));
//        dispatcher.addAudioProcessor(new SegmentWaveformWriter(format, new File(targetDir, splitClause.get(1) + ".wav").getPath(),
//            estMedianFrame, dispatcher.durationInFrames() - releaseFrames, 0, 0));
        dispatcher.run();
    }
}
