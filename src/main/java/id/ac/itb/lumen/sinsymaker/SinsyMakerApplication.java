package id.ac.itb.lumen.sinsymaker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

import java.io.File;

@SpringBootApplication
@Profile("sinsymaker")
public class SinsyMakerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SinsyMakerApplication.class)
                .profiles("sinsymaker")
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        final LineExtractor lineExtractor = new LineExtractor();
        lineExtractor.sourceAudioFile = new File("/together/project_amanah/lumen/speech/expressive/dongeng-bangau.wav");
        lineExtractor.subtitleFile = new File("/together/project_amanah/lumen/speech/expressive/dongeng-bangau.ass");
        lineExtractor.init();
        lineExtractor.extractAll();
    }
}
