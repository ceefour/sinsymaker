package id.ac.itb.lumen.sinsymaker;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

import java.io.File;

@SpringBootApplication
@Profile("voiceconcat")
public class VoiceConcatApp implements CommandLineRunner {

    public static void main(String[] args) {
        new SpringApplicationBuilder(VoiceConcatApp.class)
                .profiles("voiceconcat")
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        File passport = new File("/media/ceefour/passport");
        if (!passport.exists()) {
            passport = new File("F:/");
            Preconditions.checkState(passport.exists(), "Cannot find passport path");
        }

        final VoiceConcat voiceConcat = new VoiceConcat();
        voiceConcat.concatVoice(
                new File(passport, "project_passport/lumen/speech/expressive/db"),
                new File(passport, "project_passport/lumen/speech/expressive/clauses"),
                new File(passport, "project_passport/lumen/speech/expressive/words"),
//                ImmutableList.of("yosaku3", "menjual", "bangau2", "yang", "bernama", "gunung2"));
//                ImmutableList.of("yosaku3", "pulang", "dari", "kota", "hingga", "bernama", "bangau2", "bakar", "yosaku5"));
                // same prosody, really?
                ImmutableList.of("bekerja", "berdiri", "berjalan")
        );
    }
}
