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
        lineExtractor.sourceAudioFile = new File("/together/project_amanah/expressive/Dongeng_Anak_Pengantar_Tidur_Balas_Budi_Burung_Bangau.ogg");
        lineExtractor.subtitleFile = new File("/together/project_amanah/expressive/dongeng-bangau.ass");

    }
}
