package id.ac.itb.lumen.sinsymaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import subtitleFile.FormatASS;
import subtitleFile.TimedTextObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by ceefour on 02/03/15.
 */
public class SubtitleReader {

    private static final Logger log = LoggerFactory.getLogger(SubtitleReader.class);

    public TimedTextObject read(File file) {
        final FormatASS formatAss = new FormatASS();
        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(file))) {
            final TimedTextObject tto = formatAss.parseFile(file.toString(), is);
            log.info("{} Captions: {}", tto.captions.size(), tto.captions.keySet());
            return tto;
        } catch (java.io.IOException e) {
            throw new RuntimeException("Cannot read " + file, e);
        }
    }

}
