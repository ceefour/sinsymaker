package id.ac.itb.lumen.sinsymaker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

import java.io.File;

@SpringBootApplication
@Profile("readsubtitle")
public class ReadSubtitleApp implements CommandLineRunner {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ReadSubtitleApp.class)
                .profiles("readsubtitle")
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        final SubtitleReader subtitleReader = new SubtitleReader();
        subtitleReader.read(new File("/together/project_amanah/lumen/speech/expressive/dongeng-bangau.ass"));
    }
}
