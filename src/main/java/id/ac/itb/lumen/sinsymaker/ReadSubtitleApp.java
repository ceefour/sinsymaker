package id.ac.itb.lumen.sinsymaker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class ReadSubtitleApp implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ReadSubtitleApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        final SubtitleReader subtitleReader = new SubtitleReader();
        subtitleReader.read(new File("/together/project_amanah/expressive/dongeng-bangau.ass"));
    }
}
