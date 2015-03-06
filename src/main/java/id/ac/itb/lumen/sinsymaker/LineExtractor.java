package id.ac.itb.lumen.sinsymaker;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import net.beadsproject.beads.data.Sample;
import net.beadsproject.beads.data.SampleManager;
import net.beadsproject.beads.data.audiofile.AudioFileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import subtitleFile.Caption;
import subtitleFile.TimedTextObject;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Extracts each subtitle line from an audio into separate audio files,
 * with intro and outro padding (typically -500ms and +500ms).
 * Created by ceefour on 02/03/15.
 */
public class LineExtractor {

    private static Logger log = LoggerFactory.getLogger(LineExtractor.class);

    /**
     * OGG or WAV PCM format.
     */
    File sourceAudioFile;
    /**
     * SubStation Alpha v4 format.
     */
    File subtitleFile;
    private TimedTextObject subtitle;

    public static final int ATTACK_MS = 500;
    public static final int RELEASE_MS = 500;
    public static final Pattern K_REPLACER = Pattern.compile("[{]\\\\k\\d+[}]");

    public void init() {
        final SubtitleReader subtitleReader = new SubtitleReader();
        subtitle = subtitleReader.read(subtitleFile);
    }

    /**
     * Extract all captions using offline processing, without {@link net.beadsproject.beads.core.AudioContext}.
     * @param destDir
     */
    public void extractAll(File destDir) {
        destDir.mkdirs();

        final Sample sourceSample = Preconditions.checkNotNull(SampleManager.sample(sourceAudioFile.getPath()),
                "Cannot load '%s', either file does not exist or cannot handle audio format", sourceAudioFile);

        for (final Caption caption : subtitle.captions.values()) {
            int captionLength = caption.end.getMseconds() - caption.start.getMseconds();
            if (captionLength <= 0) {
                log.info("Skipping incomplete caption {}", caption);
                continue;
            }
            log.info("Processing caption {}", caption);
            final String captionMarkup = caption.content;
            final String captionText = K_REPLACER.matcher(captionMarkup).replaceAll("");

            Range<Integer> strictFrames = Range.closedOpen(
                    Math.round(sourceSample.getSampleRate() * caption.start.getMseconds() / 1000f),
                    Math.round(sourceSample.getSampleRate() * caption.end.getMseconds() / 1000f) );
            final long maxAttackFrames = strictFrames.lowerEndpoint();
            final long maxReleaseFrames = sourceSample.getNumFrames() - strictFrames.upperEndpoint();
            int attackFrames = (int) Math.min(Math.round(ATTACK_MS / 1000f * sourceSample.getSampleRate()), maxAttackFrames);
            int releaseFrames = (int) Math.min(Math.round(RELEASE_MS / 1000f * sourceSample.getSampleRate()), maxReleaseFrames);
            int frameStart = strictFrames.lowerEndpoint() - attackFrames;
            int frameEndExc = strictFrames.upperEndpoint() + releaseFrames;
            final File file = new File(destDir, captionText.toLowerCase().replace(' ', '-') + ".wav");
            double lengthInMs = (frameEndExc - frameStart) * 1000d / sourceSample.getSampleRate();
            log.info("Recording [{},{}) ({} ms) from strict {} to {} for «{}» ...",
                    frameStart, frameEndExc, lengthInMs, strictFrames, file, captionMarkup);
            try {
                final float[][] outFrames = new float[1][frameEndExc - frameStart]; // mono
                sourceSample.getFrames(frameStart, outFrames);

                final Sample outSample = new Sample(lengthInMs, 1, sourceSample.getSampleRate());
                outSample.putFrames(0, outFrames);
                outSample.write(file.getPath(), AudioFileType.WAV);
                // Unfortunately we can't have OGG writer yet, but... we can stream RAW PCM to a avconv pipe and generate OGG Vorbis there
//                final RecordToFile recordToFile = new RecordToFile(ac, 1, file, AudioFileFormat.Type.WAVE);
            } catch (Exception e) {
                throw new RuntimeException(String.format("Cannot record [%s,%s) to %s (source length is %s)",
                        frameStart, frameEndExc, file, sourceSample.getNumFrames()), e);
            }
        }
    }

}
