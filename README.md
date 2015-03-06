# sinsymaker
HTS Voice creator from arbitrary karaoke song, generates singer library usable by Sinsy singing synthesizer

## Beads

Until [Beads](http://www.beadsproject.net/) is in Maven Central, you'll need to install it first.

1. Download [Beads](http://www.beadsproject.net/)
2. Install the main JARs:

        # Windows
        mvn install:install-file -Dfile=D:/beads/library/beads.jar -Dpackaging=jar -DgroupId=net.beadsproject -DartifactId=beads -Dversion=1.02
        mvn install:install-file -Dfile=D:/beads/library/beads-io.jar -Dpackaging=jar -DgroupId=net.beadsproject -DartifactId=beads-io -Dversion=1.02
        # Linux
        mvn install:install-file -Dfile=/media/ceefour/passport/project_passport/lumen/speech/beads/beads/library/beads.jar -Dpackaging=jar -DgroupId=net.beadsproject -DartifactId=beads -Dversion=1.02
        mvn install:install-file -Dfile=/media/ceefour/passport/project_passport/lumen/speech/beads/beads/library/beads-io.jar -Dpackaging=jar -DgroupId=net.beadsproject -DartifactId=beads-io -Dversion=1.02

3. Package and install the "source" JARs:

        # I already generated those:
        #jar -cvf F:/project_passport/lumen/speech/beads/beads-1.02-sources.jar -C "D:/beads/src/beads_main" .
        #jar -cvf F:/project_passport/lumen/speech/beads/beads-io-1.02-sources.jar -C "D:/beads/src/beads_io" .
        # All you have to is install them:
        # Windows
        mvn install:install-file -Dfile=F:/project_passport/lumen/speech/beads/beads-1.02-sources.jar -Dpackaging=jar -DgroupId=net.beadsproject -DartifactId=beads -Dversion=1.02 -Dclassifier=sources
        mvn install:install-file -Dfile=F:/project_passport/lumen/speech/beads/beads-io-1.02-sources.jar -Dpackaging=jar -DgroupId=net.beadsproject -DartifactId=beads-io -Dversion=1.02 -Dclassifier=sources
        # Linux
        mvn install:install-file -Dfile=/media/ceefour/passport/project_passport/lumen/speech/beads/beads-1.02-sources.jar -Dpackaging=jar -DgroupId=net.beadsproject -DartifactId=beads -Dversion=1.02 -Dclassifier=sources
        mvn install:install-file -Dfile=/media/ceefour/passport/project_passport/lumen/speech/beads/beads-io-1.02-sources.jar -Dpackaging=jar -DgroupId=net.beadsproject -DartifactId=beads-io -Dversion=1.02 -Dclassifier=sources

## VorbisSPI

There's one file missing for vorbisspi in Maven, so:

    mvn install:install-file -Dfile=/media/ceefour/passport/project_passport/lumen/speech/VorbisSPI1.0.3/lib/jogg-0.0.7.jar -DgroupId=com.jcraft -DartifactId=jogg -Dversion=0.0.7 -Dpackaging=jar

## TarsosDSP (TODO: replace with Beads)

Until [TarsosDSP](https://github.com/JorenSix/TarsosDSP) is in Maven Central, you'll need to install it first.

1. Download [TarsosDSP](https://github.com/JorenSix/TarsosDSP)
2. Install the main JAR:

        mvn install:install-file -Dfile=/media/ceefour/passport/project_passport/lumen/speech/TarsosDSP-2.0-bin.jar -Dpackaging=jar -DgroupId=be.tarsos.dsp -DartifactId=tarsosdsp -Dversion=2.0

3. Install the "source" JAR (it actually contains both `.class` and `.java` files):

        mvn install:install-file -Dfile=/media/ceefour/passport/project_passport/lumen/speech/TarsosDSP-2.0-with-sources.jar -Dpackaging=jar -DgroupId=be.tarsos.dsp -DartifactId=tarsosdsp -Dversion=2.0 -Dclassifier=sources

## Audio Output Format

We'll be using ACID Loop File format (i.e. enhanced WAV) when generating all output audios.

Thanks, I've found a RIFF viewer, and specification of the riff chunks now.
In my example wav, I see a chunk called 'acid' which is 24 bytes. I suppose this will contain the tempo information, but I haven't figured out yet how this field is structured.

Update: My test file had tempo 138.00 BPM
I couldn't find 138 either in asci or in integer format in the acid tag, but 138 appears to be 00 00 0A 43 in floating point format, and this were exactly the last 4 bytes of the acid chunk.
Now I still need to find out if the tempo is at a fixed offset in the tag, or if there's some other way to know where the tempo is located.
The acid chunk that Fruity Loops created was 24 bytes long btw.

Via http://www.kvraudio.com/forum/viewtopic.php?p=3061898#p3061898 :

    ** The acid chunk goes a little something like this:
    **
    ** 4 bytes          'acid'
    ** 4 bytes (int)     length of chunk starting at next byte
    **
    ** 4 bytes (int)     type of file:
    **        this appears to be a bit mask,however some combinations
    **        are probably impossible and/or qualified as "errors"
    **
    **        0x01 On: One Shot         Off: Loop
    **        0x02 On: Root note is Set Off: No root
    **        0x04 On: Stretch is On,   Off: Strech is OFF
    **        0x08 On: Disk Based       Off: Ram based
    **        0x10 On: ??????????       Off: ????????? (Acidizer puts that ON)
    **
    ** 2 bytes (short)      root note
    **        if type 0x10 is OFF : [C,C#,(...),B] -> [0x30 to 0x3B]
    **        if type 0x10 is ON  : [C,C#,(...),B] -> [0x3C to 0x47]
    **         (both types fit on same MIDI pitch albeit different octaves, so who cares)
    **
    ** 2 bytes (short)      ??? always set to 0x8000
    ** 4 bytes (float)      ??? seems to be always 0
    ** 4 bytes (int)        number of beats
    ** 2 bytes (short)      meter denominator   //always 4 in SF/ACID
    ** 2 bytes (short)      meter numerator     //always 4 in SF/ACID
    **                      //are we sure about the order?? usually its num/denom
    ** 4 bytes (float)      tempo

TBD: Use FluidSynth's / GrandOrgue's format?
TBD: RIFF Wave Cue-Point chunks: http://sharkysoft.com/archive/lava/docs/javadocs/lava/riff/wave/doc-files/riffwave-content.htm

## Legacy Documentation

### JMathStudio -- NO LONGER USED

Until [JMathStudio](http://sourceforge.net/projects/jmathstudio/) is in Maven Central, you'll need to install it first.

1. Download [JMathStudio](http://sourceforge.net/projects/jmathstudio/) ZIP and extract to `~/tmp`.
2. Install the main JAR:

        mvn install:install-file -Dfile=$HOME/tmp/JMathStudio_Package/Bin/JMathStudio.jar -Dpackaging=jar -DgroupId=org.jmathstudio -DartifactId=jmathstudio -Dversion=1.2.0

3. Create the Javadoc JAR then install it:

        jar -cvf ~/tmp/jmathstudio-1.2.0-javadoc.jar -C "$HOME/tmp/JMathStudio_Package/API Doc" .
        mvn install:install-file -Dfile=$HOME/tmp/jmathstudio-1.2.0-javadoc.jar -Dpackaging=jar -DgroupId=org.jmathstudio -DartifactId=jmathstudio -Dversion=1.2.0 -Dclassifier=javadoc

