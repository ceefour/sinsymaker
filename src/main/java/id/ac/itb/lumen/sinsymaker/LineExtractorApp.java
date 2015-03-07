package id.ac.itb.lumen.sinsymaker;

import com.google.common.base.Preconditions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

import java.io.File;

@SpringBootApplication
@Profile("lineextractor")
public class LineExtractorApp implements CommandLineRunner {

    public static void main(String[] args) {
        new SpringApplicationBuilder(LineExtractorApp.class)
                .profiles("lineextractor")
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        File passport = new File("/media/ceefour/passport");
        if (!passport.exists()) {
            passport = new File("F:/");
            Preconditions.checkState(passport.exists(), "Cannot find passport path");
        }

        final LineExtractor lineExtractor = new LineExtractor();
        lineExtractor.sourceAudioFile = new File(passport, "project_passport/lumen/speech/expressive/dongeng-bangau.wav");
//        lineExtractor.sourceAudioFile = new File(passport, "project_passport/lumen/speech/expressive/dongeng-bangau.ogg");
        lineExtractor.subtitleFile = new File(passport, "project_passport/lumen/speech/expressive/dongeng-bangau.ass");
        lineExtractor.init();
        lineExtractor.extractAll(
                new File(passport, "project_passport/lumen/speech/expressive/db"),
                new File(passport, "project_passport/lumen/speech/expressive/clauses"),
                new File(passport, "project_passport/lumen/speech/expressive/words"));
    }
}
