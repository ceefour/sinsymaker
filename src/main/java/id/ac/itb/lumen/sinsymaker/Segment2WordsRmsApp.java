package id.ac.itb.lumen.sinsymaker;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import com.google.common.base.Splitter;
import com.google.common.collect.Range;
import net.beadsproject.beads.analysis.FeatureExtractor;
import net.beadsproject.beads.analysis.featureextractors.*;
import net.beadsproject.beads.analysis.segmenters.ShortFrameSegmenter;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.core.TimeStamp;
import net.beadsproject.beads.data.Pitch;
import net.beadsproject.beads.data.Sample;
import net.beadsproject.beads.data.SampleManager;
import net.beadsproject.beads.ugens.*;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@Profile("segment2wordsrms")
public class Segment2WordsRmsApp implements CommandLineRunner {

    private static Logger log = LoggerFactory.getLogger(Segment2WordsRmsApp.class);
    
    public static void main(String[] args) {
        new SpringApplicationBuilder(Segment2WordsRmsApp.class)
                .profiles("segment2wordsrms")
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        final File clauseAudioFile = new File("F:/project_passport/lumen/speech/expressive/clauses/perangkap oleh.wav");
//        final File oggFile = new File("F:/project_passport/lumen/speech/expressive/dongeng-bangau.ogg");
        final File targetDir = new File("F:/project_passport/lumen/speech/expressive/words");
        targetDir.mkdirs();

        final String clause = FilenameUtils.getBaseName(clauseAudioFile.getName());
        final List<String> splitClause = Splitter.on(' ').splitToList(clause);
        double estMedianPos = splitClause.get(0).length() * 1d / (splitClause.get(0).length() + splitClause.get(1).length());

        final int ATTACK_MS = 500;
        final int RELEASE_MS = 500;

        final Sample sample = SampleManager.sample(clauseAudioFile.getPath());
        log.info("Sample {} rate={} has {} frames", sample.getFileName(), sample.getSampleRate(), sample.getNumFrames());
        final int attackFrames = Math.round(ATTACK_MS / 1000f * sample.getSampleRate());
        final int releaseFrames = Math.round(RELEASE_MS / 1000f * sample.getSampleRate());
        long estMedianFrame = attackFrames + Math.round(estMedianPos * (sample.getNumFrames() - attackFrames - releaseFrames));
        log.info("estMedianFrame={}", estMedianFrame);

        final AudioContext ac = new AudioContext();
        log.info("AudioContext sampleRate={}", ac.getSampleRate());

        final SamplePlayer player = new SamplePlayer(ac, sample);
        player.setKillOnEnd(true);
        player.setEndListener(new Bead() {
            @Override
            protected void messageReceived(Bead message) {
                ac.stop();
            }
        });

        final ShortFrameSegmenter segmenter = new ShortFrameSegmenter(ac);
        segmenter.addInput(player);
        ac.out.addDependent(segmenter);
        log.info("bufferSize={}. Segmenter chunkSize={} hopSize={}", ac.getBufferSize(), segmenter.getChunkSize(), segmenter.getHopSize());
        final FFT fft = new FFT();
        segmenter.addListener(fft);
        final PowerSpectrum powerSpectrum = new PowerSpectrum();
        fft.addListener(powerSpectrum);
        
        powerSpectrum.addListener(new Frequency(ac.getSampleRate()) {
            @Override
            public synchronized void process(TimeStamp startTime, TimeStamp endTime, float[] powerSpectrum) {
                super.process(startTime, endTime, powerSpectrum);
                log.info("time={}..{} {} -> Length={} Freq: {} Note: {}", 
                        startTime.getTimeSamples(), endTime.getTimeSamples(),
                        powerSpectrum.length, powerSpectrum, getFeatures(), Pitch.ftom(getFeatures()));
            }
        });

        final SpectralDifference spectralDiff = new SpectralDifference(ac.getSampleRate());
        powerSpectrum.addListener(spectralDiff);
        // The voiced speech of a typical adult male will have a fundamental frequency from 85 to 180 Hz, and that of a typical adult female from 165 to 255 Hz.
        // the soprano Mado Robin, who was known for her exceptionally high voice, sang a number of compositions created especially to exploit her highest notes, reaching C7.
        // In Paul Mealor's choral work, De Profundis, the bass soloist is asked to sing an E1.
        // E1 = 41 Hz
        // C7 = 2093 Hz
        spectralDiff.setFreqWindow(41f, 2093f);

        AtomicInteger specdiffIdx = new AtomicInteger();
        final float[] specdiffs = new float[(int) (sample.getNumFrames() / segmenter.getChunkSize() + 1)];
        spectralDiff.addListener(new FeatureExtractor<Float, Float>() {
            @Override
            public void process(TimeStamp startTime, TimeStamp endTime, Float data) {
                log.info("{} {}..{} Specdiff {}", specdiffIdx.get(), startTime.getTimeSamples(), endTime.getTimeSamples(),
                        String.format("%.5f", data));
                specdiffs[specdiffIdx.get()] = data;
                specdiffIdx.incrementAndGet();
            }
        });
        final Map<String, Float> peaks = new LinkedHashMap<>();
        final PeakDetector peakDetector = new PeakDetector();
        spectralDiff.addListener(peakDetector);
        peakDetector.addMessageListener(new Bead() {
            @Override
            protected void messageReceived(Bead message) {
                final long msgTime = ac.generateTimeStamp(0).getTimeSamples();
                log.info("dapet at {}: {}, lastOnset={} features={} featureDesc={}",
                        msgTime, message.getName(), peakDetector.getLastOnsetValue(), peakDetector.getFeatures(),
                        peakDetector.getFeatureDescriptions());
                final String humanTime = String.format("%d/%.3fms", msgTime, ac.generateTimeStamp(0).getTimeMS());
                peaks.put(humanTime, peakDetector.getFeatures());
            }
        });
        
        final Gain g = new Gain(ac, 1, 1f);
        g.addInput(player);
        ac.out.addInput(g);
//        ac.start();
        
//        Thread.currentThread().join();
        
        ac.runNonRealTime();
        
        log.info("{} peaks: {}", peaks.size(), peaks);
        
        // now we can analyze from specdiffs
        final int searchRadius = (int) (0.500 * sample.getSampleRate() / segmenter.getChunkSize());
        final float searchThreshold = 10f;
        int estMedianChunk = (int) (estMedianFrame / segmenter.getChunkSize());
        // analyze: lower is "more quiet", we need to find the most quiet
        float leftLoudness = 0f;
        Integer leftEndChunk = null;
        Integer rightStartChunk = null;
        float rightLoudness = 0f;
        for (int chunk = Math.max(estMedianChunk - searchRadius, 0); chunk <= estMedianChunk; chunk++) {
            if (specdiffs[chunk] < searchThreshold) {
                leftLoudness += specdiffs[chunk];
                leftEndChunk = chunk;
            } else {
                leftLoudness += searchThreshold; // penalty
            }
        }
        for (int chunk = estMedianChunk; chunk <= Math.min(estMedianChunk + searchRadius, specdiffs.length - 1); chunk++) {
            if (specdiffs[chunk] < searchThreshold) {
                rightLoudness += specdiffs[chunk];
                if (rightStartChunk == null) {
                    rightStartChunk = chunk;
                }
            } else {
                rightLoudness += searchThreshold; // penalty
            }
        }
        log.info("Quietness: Left={} end {} Right={} start {}", 
                leftLoudness, leftEndChunk, rightLoudness, rightStartChunk);
        final Range<Integer> cutFrames;
        if (leftLoudness < rightLoudness) {
            // left is quieter, search contiguous to the left
            int leftStartChunk = leftEndChunk;
            for (int chunk = leftEndChunk; chunk >= Math.max(leftEndChunk - searchRadius, 0); chunk--) {
                if (specdiffs[chunk] < searchThreshold) {
                    leftStartChunk = chunk;
                } else {
                    break;
                }
            }
            cutFrames = Range.closed(leftStartChunk * segmenter.getChunkSize(), leftEndChunk * segmenter.getChunkSize());
        } else {
            // right is quieter
            int rightEndChunk = rightStartChunk;
            for (int chunk = rightStartChunk; chunk <= Math.min(rightStartChunk + searchRadius, specdiffs.length - 1); chunk++) {
                if (specdiffs[chunk] < searchThreshold) {
                    rightEndChunk = chunk;
                } else {
                    break;
                }
            }
            cutFrames = Range.closed(rightStartChunk * segmenter.getChunkSize(), rightEndChunk * segmenter.getChunkSize());
        }
        final int middleCutFrame = (cutFrames.lowerEndpoint() + cutFrames.upperEndpoint()) / 2;
        log.info("Cut frame={} from {} ({} chunks or {} chunk-frames)",
                middleCutFrame, cutFrames, specdiffs.length, segmenter.getChunkSize() * specdiffs.length);
        
//        final TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(44100, 16, 1, true, false);
//
//        // with attack + release
//        dispatcher.addAudioProcessor(new SegmentWaveformWriter(format, new File(targetDir, splitClause.get(0) + ".wav").getPath(),
//            0, estMedianFrame + releaseFrames, attackFrames, releaseFrames));
//        dispatcher.addAudioProcessor(new SegmentWaveformWriter(format, new File(targetDir, splitClause.get(1) + ".wav").getPath(),
//            estMedianFrame - attackFrames, dispatcher.durationInFrames(), attackFrames, releaseFrames));
//        // strict splitter
////        dispatcher.addAudioProcessor(new SegmentWaveformWriter(format, new File(targetDir, splitClause.get(0) + ".wav").getPath(),
////            attackFrames, estMedianFrame, 0, 0));
////        dispatcher.addAudioProcessor(new SegmentWaveformWriter(format, new File(targetDir, splitClause.get(1) + ".wav").getPath(),
////            estMedianFrame, dispatcher.durationInFrames() - releaseFrames, 0, 0));
//        dispatcher.run();
    }
}
