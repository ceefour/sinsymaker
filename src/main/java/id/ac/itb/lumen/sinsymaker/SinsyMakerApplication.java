package id.ac.itb.lumen.sinsymaker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class SinsyMakerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SinsyMakerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        final LineExtractor lineExtractor = new LineExtractor();
        lineExtractor.sourceAudioFile = new File("/together/project_amanah/expressive/Dongeng_Anak_Pengantar_Tidur_Balas_Budi_Burung_Bangau.ogg");
        lineExtractor.subtitleFile = new File("/together/project_amanah/expressive/dongeng-bangau.ass");

    }
}
