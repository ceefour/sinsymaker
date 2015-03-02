package id.ac.itb.lumen.sinsymaker;

import java.io.File;

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

}
