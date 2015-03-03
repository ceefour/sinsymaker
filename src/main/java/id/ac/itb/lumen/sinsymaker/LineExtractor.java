package id.ac.itb.lumen.sinsymaker;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import subtitleFile.Caption;
import subtitleFile.TimedTextObject;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Extracts each subtitle line from an audio into separate audio files,
 * with intro and outro padding (typically -500ms and +500ms).
 * Created by ceefour on 02/03/15.
 */
public class LineExtractor {

    /**
     * OGG or WAV PCM format.
     */
    File sourceAudioFile;
    /**
     * SubStation Alpha v4 format.
     */
    File subtitleFile;
    private FloatBuffer audio;
    private TimedTextObject subtitle;
    private TarsosDSPAudioFormat format;

    public static final int ATTACK_MS = 500;
    public static final int DECAY_MS = 500;

    public void init() {
        format = new TarsosDSPAudioFormat(44100, 16, 1, true, false);
        final SubtitleReader subtitleReader = new SubtitleReader();
        subtitle = subtitleReader.read(subtitleFile);
    }

    public void extractAll(File destDir) {
        destDir.mkdirs();
        final ArrayList<SegmentWaveformWriter> segmentWriters = new ArrayList<>();
        for (final Caption caption : subtitle.captions.values()) {
            int strictFrameStart = Math.round(format.getSampleRate() * caption.start.getMseconds() / 1000f);
            int strictFrameEnd = Math.round(format.getSampleRate() * caption.end.getMseconds() / 1000f);
            int frameStart = Math.round(strictFrameStart - ATTACK_MS / 1000f * format.getSampleRate());
            int frameEnd = Math.round(strictFrameEnd + DECAY_MS / 1000f * format.getSampleRate());
            final File file = new File(destDir, caption.content + ".wav");
            final SegmentWaveformWriter segmentWriter = new SegmentWaveformWriter(format, file.getPath(), frameStart, frameEnd, ATTACK_MS, DECAY_MS);
            segmentWriters.add(segmentWriter);
        }

        try {
            final AudioDispatcher dispatcher = AudioDispatcherFactory.fromFile(sourceAudioFile, 2048, 0);
            for (final SegmentWaveformWriter writer : segmentWriters) {
                dispatcher.addAudioProcessor(writer);
            }
            dispatcher.run();
        } catch (Exception e) {
            throw new RuntimeException("Cannot process " + sourceAudioFile, e);
        }
    }

}
