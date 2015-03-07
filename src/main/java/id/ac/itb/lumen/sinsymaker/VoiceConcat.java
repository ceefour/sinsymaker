package id.ac.itb.lumen.sinsymaker;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import id.ac.itb.lumen.sinsymaker.core.Clause;
import id.ac.itb.lumen.sinsymaker.core.SpeechGraph;
import id.ac.itb.lumen.sinsymaker.core.Voice;
import id.ac.itb.lumen.sinsymaker.core.Word;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.core.IOAudioFormat;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;
import net.beadsproject.beads.data.Sample;
import net.beadsproject.beads.data.SampleManager;
import net.beadsproject.beads.ugens.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soluvas.commons.SlugUtils;
import org.soluvas.json.JsonUtils;
import subtitleFile.Caption;
import subtitleFile.TimedTextObject;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Concatenates voices.
 */
public class VoiceConcat {

    private static Logger log = LoggerFactory.getLogger(VoiceConcat.class);

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

    protected static class Clip {
        Voice voice;
        Sample sample;
        Envelope envelope;
        Gain gain;
        SamplePlayer player;

        public Clip(Voice voice, Sample sample, Envelope envelope, Gain gain, SamplePlayer player) {
            this.voice = voice;
            this.sample = sample;
            this.envelope = envelope;
            this.gain = gain;
            this.player = player;
        }
    }

    /**
     * Extract all captions using offline processing, without {@link net.beadsproject.beads.core.AudioContext}.
     * @param clausesDir
     */
    public void concatVoice(File dbPath, File clausesDir, File wordsDir,
                            List<String> voiceIds) {

        final File speechJsonFile = new File(dbPath, "speech.json");
        final SpeechGraph speechGraph;
        try {
            speechGraph = JsonUtils.mapper.readValue(speechJsonFile, SpeechGraph.class);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read speech database " + speechJsonFile);
        }

        final List<Sample> samples = new ArrayList<>();
        for (final String voiceId : voiceIds) {
            final Voice voice = Preconditions.checkNotNull(speechGraph.getVoices().get(voiceId),
                    "Cannot get voice '%s'. %s available voices: %s",
                    voiceId, speechGraph.getVoices().size(), speechGraph.getVoices().keySet());
            final File voiceFile = new File(voice.getKind() == Voice.Kind.CLAUSE ? clausesDir : wordsDir, voice.getId() + ".wav");
            log.info("Loading {} ...", voiceFile);
            final Sample sample = Preconditions.checkNotNull(SampleManager.sample(voiceFile.getPath()),
                    "Cannot load sample '%s'", voiceFile);
            samples.add(sample);
        }

        // concat
        final IOAudioFormat format = new IOAudioFormat(48000, 16, 2, 2);
        final AudioContext ac = new AudioContext(new JavaSoundAudioIO(), AudioContext.DEFAULT_BUFFER_SIZE, format);
        final List<Clip> samplePlayers = new ArrayList<>();
        final ArrayDeque<Clip> playerQueue = new ArrayDeque<>();
        for (int i = 0; i < voiceIds.size(); i++) {
            final Voice voice = speechGraph.getVoices().get(voiceIds.get(i));
            final Sample sample = samples.get(i);
            final SamplePlayer samplePlayer = new SamplePlayer(ac, sample);
            samplePlayer.setName(voice.getId());
            samplePlayer.setKillOnEnd(true);
//            samplePlayer.setLoopStart(new Static(ac, voice.getBodyStart() / 44.1f));
//            samplePlayer.setLoopEnd(new Static(ac, (voice.getBodyStart() + voice.getBodyLength()) / 44.1f ));
//            samplePlayer.setToLoopStart();
            samplePlayer.setRate(new Static(ac, 1f));
//            samplePlayer.setKillListener(new Bead() {
//                @Override
//                protected void messageReceived(Bead message) {
//                    super.messageReceived(message);
//                    nextPlayer(ac, playerQueue);
//                }
//            });

            final Envelope envelope = new Envelope(ac, 0f);
            envelope.addSegment(0f, (float) ac.samplesToMs(voice.getPreLength() * 0.95));
            envelope.addSegment(1f, (float) ac.samplesToMs(voice.getPreLength() * 0.05));
            envelope.addSegment(1f, (float) ac.samplesToMs(voice.getBodyLength()));
            envelope.addSegment(0f, (float) ac.samplesToMs(voice.getPostLength() * 0.2));
            final Gain gain = new Gain(ac, 1, envelope);
            gain.addInput(samplePlayer);
            final DelayTrigger bodyNearEndTrigger = new DelayTrigger(ac,
                    ac.samplesToMs(voice.getPreLength() + voice.getBodyLength()) - 350, new Bead() {
                @Override
                protected void messageReceived(Bead message) {
                    super.messageReceived(message);
                    nextPlayer(ac, playerQueue);
                }
            });
            gain.addDependent(bodyNearEndTrigger);
            gain.pause(true);
            ac.out.addInput(gain);

            samplePlayers.add(new Clip(voice, sample, envelope, gain, samplePlayer));
        }
        ac.start();

        playerQueue.addAll(samplePlayers);
        nextPlayer(ac, playerQueue);
    }

    protected void nextPlayer(AudioContext ac, Queue<Clip> playerQueue) {
        final Clip clip = playerQueue.poll();
        if (clip != null) {
            log.info("Playing ({}) {} ({}) ...", clip.voice.getPrevWordId(), clip.voice.getId(), clip.voice.getNextWordId());
            clip.gain.start();
        } else {
            log.info("No more SamplePlayers, waiting a bit before stopping...");
            ac.out.addDependent(new DelayTrigger(ac, 2000d, new Bead() {
                @Override
                protected void messageReceived(Bead message) {
                    super.messageReceived(message);
                    log.info("Stopping AudioContext");
                    ac.stop();
                }
            }));
        }
    }

}
