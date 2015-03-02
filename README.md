# sinsymaker
HTS Voice creator from arbitrary karaoke song, generates singer library usable by Sinsy singing synthesizer

## JMathStudio

Until [JMathStudio](http://sourceforge.net/projects/jmathstudio/) is in Maven Central, you'll need to install it first.

1. Download [JMathStudio](http://sourceforge.net/projects/jmathstudio/) ZIP and extract to `~/tmp`.
2. Install the main JAR:

        mvn install:install-file -Dfile=$HOME/tmp/JMathStudio_Package/Bin/JMathStudio.jar -Dpackaging=jar -DgroupId=org.jmathstudio -DartifactId=jmathstudio -Dversion=1.2.0

3. Create the Javadoc JAR then install it:

        jar -cvf ~/tmp/jmathstudio-1.2.0-javadoc.jar -C "$HOME/tmp/JMathStudio_Package/API Doc" .
        mvn install:install-file -Dfile=$HOME/tmp/jmathstudio-1.2.0-javadoc.jar -Dpackaging=jar -DgroupId=org.jmathstudio -DartifactId=jmathstudio -Dversion=1.2.0 -Dclassifier=javadoc
