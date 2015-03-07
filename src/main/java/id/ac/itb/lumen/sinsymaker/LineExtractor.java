package id.ac.itb.lumen.sinsymaker;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import id.ac.itb.lumen.sinsymaker.core.Clause;
import id.ac.itb.lumen.sinsymaker.core.SpeechGraph;
import id.ac.itb.lumen.sinsymaker.core.Voice;
import id.ac.itb.lumen.sinsymaker.core.Word;
import net.beadsproject.beads.data.Sample;
import net.beadsproject.beads.data.SampleManager;
import net.beadsproject.beads.data.audiofile.AudioFileType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soluvas.commons.SlugUtils;
import org.soluvas.json.JsonUtils;
import org.soluvas.json.LowerEnumDeserializer;
import org.soluvas.json.LowerEnumModule;
import org.soluvas.json.LowerEnumSerializer;
import subtitleFile.Caption;
import subtitleFile.TimedTextObject;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts each subtitle line from an audio into separate audio files,
 * with intro and outro padding (typically -500ms and +500ms).
 * Created by ceefour on 02/03/15.
 */
public class LineExtractor {

    private static Logger log = LoggerFactory.getLogger(LineExtractor.class);

    public static final Locale INDONESIAN = Locale.forLanguageTag("id-ID");
    public static final int ATTACK_MS = 500;
    public static final int RELEASE_MS = 500;
    public static final Pattern K_REPLACER = Pattern.compile("[{]\\\\k\\d+[}]");
    public static final Pattern K_REPLACER_WORD = Pattern.compile("[{]\\\\k(\\d+)[}](.+?)(?=[{]\\\\|$)");

    public static class VoiceMeta {
        int preStart;
        int preLength;
        int bodyStart;
        int bodyLength;
        int postStart;
        int postLength;
    }

    /**
     * OGG or WAV PCM format.
     */
    File sourceAudioFile;
    /**
     * SubStation Alpha v4 format.
     */
    File subtitleFile;
    private TimedTextObject subtitle;
    private SpeechGraph speechGraph = new SpeechGraph();

    public void init() {
        LowerEnumSerializer.LOWER = false;

        final SubtitleReader subtitleReader = new SubtitleReader();
        subtitle = subtitleReader.read(subtitleFile);
    }

    protected VoiceMeta cutSegment(Sample sourceSample, Range<Integer> strictFrames, int attackInMs, int releaseInMs,
                              File file, String description) {
        final long maxAttackFrames = strictFrames.lowerEndpoint();
        final long maxReleaseFrames = sourceSample.getNumFrames() - strictFrames.upperEndpoint();
        int attackFrames = (int) Math.min(Math.round(attackInMs / 1000f * sourceSample.getSampleRate()), maxAttackFrames);
        int releaseFrames = (int) Math.min(Math.round(releaseInMs / 1000f * sourceSample.getSampleRate()), maxReleaseFrames);
        final Range<Integer> frames = Range.closedOpen(strictFrames.lowerEndpoint() - attackFrames,
                strictFrames.upperEndpoint() + releaseFrames);

        final VoiceMeta voiceMeta = new VoiceMeta();
        voiceMeta.preStart = 0;
        voiceMeta.preLength = attackFrames;
        voiceMeta.bodyStart = attackFrames;
        voiceMeta.bodyLength = strictFrames.upperEndpoint() - strictFrames.lowerEndpoint();
        voiceMeta.postStart = strictFrames.upperEndpoint();
        voiceMeta.postLength = releaseFrames;

        double lengthInMs = (frames.upperEndpoint() - frames.lowerEndpoint()) * 1000d / sourceSample.getSampleRate();
        log.info("Recording {} ({} ms) from strict {} to {} for «{}» ...",
                frames, lengthInMs, strictFrames, file, description);
        try {
            final float[][] outFrames = new float[1][frames.upperEndpoint() - frames.lowerEndpoint()]; // mono
            sourceSample.getFrames(frames.lowerEndpoint(), outFrames);

            final Sample outSample = new Sample(lengthInMs, 1, sourceSample.getSampleRate());
            outSample.putFrames(0, outFrames);
            outSample.write(file.getPath(), AudioFileType.WAV);
            // Unfortunately we can't have OGG writer yet, but... we can stream RAW PCM to a avconv pipe and generate OGG Vorbis there
//                final RecordToFile recordToFile = new RecordToFile(ac, 1, file, AudioFileFormat.Type.WAVE);

            return voiceMeta;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Cannot record %s to %s (source length is %s)",
                    frames, file, sourceSample.getNumFrames()), e);
        }
    }

    /**
     * Extract all captions using offline processing, without {@link net.beadsproject.beads.core.AudioContext}.
     * @param clausesDir
     */
    public void extractAll(File dbPath, File clausesDir, File wordsDir) {
        dbPath.mkdirs();
        clausesDir.mkdirs();
        wordsDir.mkdirs();

        final Sample sourceSample = Preconditions.checkNotNull(SampleManager.sample(sourceAudioFile.getPath()),
                "Cannot load '%s', either file does not exist or cannot handle audio format", sourceAudioFile);

        Optional<Voice> lastWordVoice = Optional.empty();
        Optional<Voice> lastClauseVoice = Optional.empty();
        Optional<Word> lastWord = Optional.empty();
        for (final Caption caption : subtitle.captions.values()) {
            int captionLength = caption.end.getMseconds() - caption.start.getMseconds();
            if (captionLength <= 0) {
                log.info("Skipping incomplete caption {}", caption);
                continue;
            }
            log.info("Processing caption {}", caption);
            final String captionMarkup = caption.rawContent;
            final String captionText = K_REPLACER.matcher(captionMarkup).replaceAll("");

            final Clause clause = new Clause();
            clause.setName(captionText.toLowerCase().trim());
            clause.setId(SlugUtils.generateId(StringUtils.rightPad(clause.getName(), 3, "0")));
            clause.setLanguage(INDONESIAN);
            speechGraph.getClauses().put(clause.getId(), clause);

            final Voice clauseVoice = new Voice();
            clauseVoice.setId(clause.getId());
            clauseVoice.setName(clause.getName());
            clauseVoice.setSpeakerId("kid_telling_stories");
            clauseVoice.setLanguage(INDONESIAN);
            clauseVoice.setKind(Voice.Kind.CLAUSE);
            clauseVoice.setClauseId(clause.getId());
            clauseVoice.setPrevWordId(lastWord.map(Word::getId).orElse(null));
            speechGraph.getVoices().put(clauseVoice.getId(), clauseVoice);

            final Range<Integer> strictFrames = Range.closedOpen(
                    Math.round(sourceSample.getSampleRate() * caption.start.getMseconds() / 1000f),
                    Math.round(sourceSample.getSampleRate() * caption.end.getMseconds() / 1000f) );
            final File clauseFile = new File(clausesDir, clauseVoice.getId() + ".wav");
            final VoiceMeta clauseVoiceMeta = cutSegment(sourceSample, strictFrames, ATTACK_MS, RELEASE_MS, clauseFile, captionMarkup);
            clauseVoice.setPreStart(clauseVoiceMeta.preStart);
            clauseVoice.setPreLength(clauseVoiceMeta.preLength);
            clauseVoice.setBodyStart(clauseVoiceMeta.bodyStart);
            clauseVoice.setBodyLength(clauseVoiceMeta.bodyLength);
            clauseVoice.setPostStart(clauseVoiceMeta.postStart);
            clauseVoice.setPostLength(clauseVoiceMeta.postLength);

            // Segment words
            final Matcher wordMatcher = K_REPLACER_WORD.matcher(captionMarkup);
            int curFrame = strictFrames.lowerEndpoint();
            if (wordMatcher.find()) {
                do {
                    final int durationInCs = Integer.parseInt(wordMatcher.group(1));
                    final int durationInFrames = Math.round(durationInCs / 100f * sourceSample.getSampleRate());
                    final String rawWord = wordMatcher.group(2);
                    final String wordName = rawWord.toLowerCase().trim().replace(",", "").replace("\"", "");

                    // avoid di -> dia
                    final String wordId = SlugUtils.generateId(StringUtils.rightPad(wordName, 3, "0"));
                    Word word = speechGraph.getWords().get(wordId);
                    if (word == null) {
                        word = new Word();
                        word.setId(wordId);
                        word.setName(wordName);
                        word.setLanguage(INDONESIAN);
                        word.setKind(Word.Kind.LANGUAGE);
                        speechGraph.getWords().put(word.getId(), word);
                    }
                    clause.getWordIds().add(wordId);
                    if (lastWordVoice.isPresent()) {
                        lastWordVoice.get().setNextWordId(wordId);
                    }
                    if (lastClauseVoice.isPresent()) {
                        lastClauseVoice.get().setNextWordId(wordId);
                    }

                    final String voiceId = SlugUtils.generateValidId(word.getId(),
                            it -> !speechGraph.getVoices().containsKey(it));
                    final Voice voice = new Voice();
                    voice.setId(voiceId);
                    voice.setName(word.getName());
                    voice.setKind(Voice.Kind.WORD);
                    voice.setLanguage(INDONESIAN);
                    voice.setPrevWordId(lastWord.map(Word::getId).orElse(null));
                    voice.setSpeakerId("kid_telling_stories");
                    speechGraph.getVoices().put(voice.getId(), voice);

                    final File wordFile = new File(wordsDir, voiceId + ".wav");
                    final Range<Integer> wordStrictFrames = Range.closedOpen(curFrame,
                            (int) Math.min(curFrame + durationInFrames, sourceSample.getNumFrames()));
                    log.info("Word '{}' ({}cs) {} ({} frames)", rawWord, durationInCs, wordStrictFrames, durationInFrames);
                    final VoiceMeta voiceMeta = cutSegment(sourceSample, wordStrictFrames, ATTACK_MS, RELEASE_MS, wordFile, rawWord);
                    voice.setPreStart(voiceMeta.preStart);
                    voice.setPreLength(voiceMeta.preLength);
                    voice.setBodyStart(voiceMeta.bodyStart);
                    voice.setBodyLength(voiceMeta.bodyLength);
                    voice.setPostStart(voiceMeta.postStart);
                    voice.setPostLength(voiceMeta.postLength);
                    curFrame += durationInFrames;

                    lastWordVoice = Optional.of(voice);
                    lastWord = Optional.of(word);
                } while (wordMatcher.find());
            } else {
                log.info("No k-replacer for {}", captionMarkup);
            }

            lastClauseVoice = Optional.of(clauseVoice);
        }

        final String speechJson = JsonUtils.asJson(speechGraph);
        final File speechJsonFile = new File(dbPath, "speech.json");
        try {
            FileUtils.write(speechJsonFile, speechJson);
        } catch (IOException e) {
            throw new RuntimeException("Cannot write speech graph to " + speechJsonFile, e);
        }

    }

}
