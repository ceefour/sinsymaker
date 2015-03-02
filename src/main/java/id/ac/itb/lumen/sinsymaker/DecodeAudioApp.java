package id.ac.itb.lumen.sinsymaker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

import java.io.File;

@SpringBootApplication
@Profile("decodeaudio")
public class DecodeAudioApp implements CommandLineRunner {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DecodeAudioApp.class)
                .profiles("decodeaudio")
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        final AudioReader reader = new AudioReader();
        reader.read(new File("/together/project_amanah/expressive/dongeng-bangau.wav"));
    }
}
