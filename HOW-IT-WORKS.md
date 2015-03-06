# How SinsyMaker Works

There are several steps from preparing to playing:

1. Preparing
2. Testing with Training Input
3. Dubbing
4. Voice Database (Singer Library) Generation
5. Playing
    a. with Training Script
    b. with Compatible Script

## Formats

We use 1 channel (Mono), 48000 Hz sample rate, 16-bit audio.
The intermediate format is WAV PCM, and JSON, TODO: with RIFF metadata.
The final format would be Ogg FLAC (`libvorbis` codec), ~406 kbps bitrate.
For pre-existing audio, 44.1 kHz sample rate with Ogg FLAC is also accepted.

* Why mono? Speech/singing has single source. We can pan the sound during play.
* Why not 44.1K/96K? While it's true we'll be modifying pitches, speech & singing is limited
  to 41-2093 Hz. 44.1K should be sufficient, but unlike FLAC/PCM, with Vorbis it's possible to have 48kHz
  with only slight increase in bitrate. Most YouTube or music are 44.1 kHz and it's OK to use 44.1 kHz
  as-is rather than upsampling. But for new recordings I recommend 48 kHz.
* Why not 24-bit? Unlike movies or music, speech as narrow dynamic amplitude range.
* Why not Vorbis? While I favor less storage requirement,
  and that we can max-out the Vorbis bitrate to hopefully get acceptable quality even with pitch-manipulation...
  In reality I experienced audible clipping/distortion, especially when original source is AAC,
  then read using Jorbis.
  FYI, Vorbis mono for 44.1kHz: 48-128 Kbps, for 48kHz: 48-160 Kbps.

## Software

1. Audio editor & recording: [Audacity](http://audacity.sourceforge.net/)
2. Subtitle editor: [Aegisub](http://www.aegisub.org/)
3. Audio conversion: [avconv](https://libav.org/avconv.html)
4. Lip sync: [Papagayo](http://www.lostmarble.com/papagayo/)
5. MusicXML composer: [MuseScore](http://musescore.org)

## Preparing

Preparation for Expressive and Singing databases are similar, but different.

TBD Format for Expressive Rythmo Band:

1. ASS
2. [VSI Paris / Chinkel S. A.'s Cappella](http://www.vsi-paris.tv/en/services/cappella/cappella)
3. **DetX**. [Phonations'](http://www.phonations.com/) / [Joker](https://github.com/Phonations/Joker): GPL License, Mac/Windows only.
    http://www.joker.io/
    Also used by [PlayR](https://github.com/MartinDelille/PlayR/wiki/Play'R-Rythmo-functional-specifications).
4. Mosaic
5. [VoiceQ](http://www.voiceq.com/): USD 3,995 per year
6. [DubStudio](http://www.dubstudio.com/)
7. [Synchronos](http://www.synchronos.fr/uk/): EUR 1,350
8. Annotation Edit

### Preparing WAV PCM Mono Audio

We exclusively use mono audio before playing/synthesis, historically because TarsosDSP only supports mono, but practically because
speech is mono. During playing/synthesis, voices can be panned, and mixed together with stereo/multichannel sources (e.g. music).
Java supports WAV PCM by default (among other less useful formats), but with VorbisSPI we can *read* (but not write) Ogg Vorbis.

If you want to downmix stereo to mono:

    avconv -i Dongeng_Anak_Pengantar_Tidur_Balas_Budi_Burung_Bangau.mp4 -vn -ac 1 dongeng-bangau.wav

If you want to take only left channel:

    avconv -i Dongeng_Anak_Pengantar_Tidur_Balas_Budi_Burung_Bangau.mp4 -vn -af pan=1:c0=c0 dongeng-bangau-left.wav

If you want to take only right channel:

    avconv -i Dongeng_Anak_Pengantar_Tidur_Balas_Budi_Burung_Bangau.mp4 -vn -af pan=1:c0=c1 dongeng-bangau-right.wav

### Preparing - Expressive

1. Optional but recommended: A video for a drama script.
    To get the OGG audio for the video, use e.g.:

        # Specifies 128k, in practice you'll get 79 kbps bitrate
        # 67.8 MiB PCM WAV -> 7.6 MiB 128k-mono-44.1kHz Vorbis
        avconv -i Dongeng_Anak_Pengantar_Tidur_Balas_Budi_Burung_Bangau.mp4 -vn -ac 1 -acodec libvorbis -b 128k dongeng-bangau.ogg

2. Find or write a drama script, or write the drama script for video in point 1.
    If you want to create a voice database, you need to analyze the most common words,
    emotions, and gestures for the target segment (i.e. Indonesian children book with animals)
    and write a drama script from that, which can be reused many times for different speakers,
    and also for speech recognition purpose.
    Indonesian word distribution (general, 10,000 words each): http://www.slideshare.net/geovedi/perbandingan-distribusi-frekuensi-kata-bahasa-indonesia-di-kompas-wikipedia-twitter-dan-kaskus
    https://github.com/ardwort/freq-dist-id
    idwiki is best, Kompas is too political.
    
    For word analysis in children stories, see [CeritaKecil.com](http://www.ceritakecil.com/) especially
    by Aesop, e.g. http://www.ceritakecil.com/cerita-dan-dongeng/Anak-Kambing-dan-Serigala-25
    see `F:\project_passport\lumen\speech\expressive\ceritakecil`
    
    After you get the word distribution, the next step is devising a script or a set of scripts
    as natural as possible, that can cover (some overlapping is fine) *most* of the words
    in all the emotions, gestures, and genders you want.
    "Most" here means about 90% of the words in a story will be covered by it.
    The rest of the words can be synthesized using mbrola-male-id1 with espeak, festival, or HTS.

2. Time each sentence and clause or gestures. Then time each word and syllable.
    Gestures are language-independent voice, like laughing, sobbing, etc.
3. Tag each clause/word/syllable with emotion and gender.

Deliverables:

1. Video
2. ASS Subtitle

## Preparing - Singing

1. Find a song with at least background music, with appropriate free/open license.
    Optional but recommended: With a karaoke video.
    Optional but recommended: with separate voice channel, so you can test.
2. Write the pitchs/notes, lyrics, and timing into MusicXML.
3. Tag each clause/word/syllable with emotion and gender.

Deliverables:

1. Video (Karaoke is better)
2. ASS Subtitle

## Testing with Training Input

1. The sound stream from the training video will be processed, segmented, and analyzed.
    Noise removal shouldn't be necessary with a good quality input.

## Dubbing/Lip Sync (Assisted Recording using Rythmo Band)

I call this dubbing because it's more similar to dubbing/voice-over an animated movie
or a foreign movie then a straightforward recording.

To know how rythmo band works: https://www.youtube.com/watch?v=3B4WilP-N7I

1. Noise Profiling
2. Playing Video, Animating Subtitles/Script, Recording
3. Noise Removal

### Noise Profiling and Removal

* [Audacity's Noise Removal - Wiki](http://wiki.audacityteam.org/wiki/Noise_Removal)
* [Audacity's NoiseRemoval.cpp](https://code.google.com/p/audacity/source/browse/audacity-src/trunk/src/effects/NoiseRemoval.cpp)
* [Audacity forum thread on Noise Removal algorithm](http://forum.audacityteam.org/viewtopic.php?f=46&t=73923)

## Playing using New Scripts
 
You can get stories from [CeritaKecil.com](http://www.ceritakecil.com/) especially
by Aesop, e.g. http://www.ceritakecil.com/cerita-dan-dongeng/Anak-Kambing-dan-Serigala-25
see `F:\project_passport\lumen\speech\expressive\ceritakecil`

## Syllable Assimilation/Coarticulation/Allophonic

Assimilated syllabes are intentionally preserved, not only because it'd be hard to separate them
in most natural sources, but also because preserving them will allow us to synthesize natural-sounding
expressive speeches and singing.

For example: di atas, di bawah. We need to have 2 "di"s to render the appropriate "di" naturally,
because "di" for "atas" will sound more like "diya", where "di" for "bawah" will sound more like "dib".

Each word is tagged by its previous word, next word, previous diphone, and next diphone.
Which will be prioritized during synthesis.

