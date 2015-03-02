# sinsymaker
HTS Voice creator from arbitrary karaoke song, generates singer library usable by Sinsy singing synthesizer

## TarsosDSP

Until [TarsosDSP](https://github.com/JorenSix/TarsosDSP) is in Maven Central, you'll need to install it first.

1. Download [TarsosDSP](https://github.com/JorenSix/TarsosDSP)
2. Install the main JAR:

        mvn install:install-file -Dfile=/together/project_amanah/lumen/speech/TarsosDSP-2.0-bin.jar -Dpackaging=jar -DgroupId=be.tarsos.dsp -DartifactId=tarsosdsp -Dversion=2.0

3. Install the "source" JAR (it actually contains both `.class` and `.java` files):

        mvn install:install-file -Dfile=/together/project_amanah/lumen/speech/TarsosDSP-2.0-with-sources.jar -Dpackaging=jar -DgroupId=be.tarsos.dsp -DartifactId=tarsosdsp -Dversion=2.0 -Dclassifier=sources

## JMathStudio -- NO LONGER USED

Until [JMathStudio](http://sourceforge.net/projects/jmathstudio/) is in Maven Central, you'll need to install it first.

1. Download [JMathStudio](http://sourceforge.net/projects/jmathstudio/) ZIP and extract to `~/tmp`.
2. Install the main JAR:

        mvn install:install-file -Dfile=$HOME/tmp/JMathStudio_Package/Bin/JMathStudio.jar -Dpackaging=jar -DgroupId=org.jmathstudio -DartifactId=jmathstudio -Dversion=1.2.0

3. Create the Javadoc JAR then install it:

        jar -cvf ~/tmp/jmathstudio-1.2.0-javadoc.jar -C "$HOME/tmp/JMathStudio_Package/API Doc" .
        mvn install:install-file -Dfile=$HOME/tmp/jmathstudio-1.2.0-javadoc.jar -Dpackaging=jar -DgroupId=org.jmathstudio -DartifactId=jmathstudio -Dversion=1.2.0 -Dclassifier=javadoc

## Preparing WAV PCM Mono Audio

TarsosDSP only supports mono. While Java only supports WAV PCM (among other less useful formats).

If you want to downmix stereo to mono:

    avconv -i Dongeng_Anak_Pengantar_Tidur_Balas_Budi_Burung_Bangau.mp4 -vn -ac 1 dongeng-bangau.wav

If you want to take only left channel:

    avconv -i Dongeng_Anak_Pengantar_Tidur_Balas_Budi_Burung_Bangau.mp4 -vn -af pan=1:c0=c0 dongeng-bangau-left.wav

If you want to take only right channel:

    avconv -i Dongeng_Anak_Pengantar_Tidur_Balas_Budi_Burung_Bangau.mp4 -vn -af pan=1:c0=c1 dongeng-bangau-right.wav
