# Prosody Analysis

Analysis is done by clustering based on number of syllables and the tone and rate of each syllable in the word.
Assimilation of phonemes between previous and next words is irrelevant,
because it is assumed that assimilation should be avoided during dubbing.

## 1-syllables

1. high-low, sad: aah
2. D-E, fast: ber
3.

## 2-syllables

1. E-C, mid-slowing: api
2. D-E / almost flat, fast-mid: api2
3. D-E / almost flat, mid-fast: atas, atas2, bangau2, bangau3
4. D-C, mid-slow: badan
4. D-C, mid-fast: bakar
5. D-CE(mod), fast-slowing: bangau

## 3-syllables

1. E-D-D, mid-fast-fast: adalah
2. E-D-C, mid-fast-slowing: angkasa
3. D-D-E, mid-fast-fast: badanmu
4. D-E-E, fast-mid-fast: bekerja, berdiri, berjalan

## 4-syllables

1. D-D-C-C, fast-fast-fast-fast: beberapa

## My own

For "pintu":

1. **SS Start Sentence**. Pintu1 mulai dibuka | Gadis mulai1 dibuka
2. **IC In Clause**. Ketika pintu2 dibuka | Ketika gadis2 yang
3. **EC End Clause**. di depan pintu3, dia...  | di depan gadis3, dia...
4. **ES End Sentence**. Ada di depan pintu4. | Ada di depan gadis4.

### Proof 1

In `/media/ceefour/passport/project_passport/lumen/speech/expressive/ketika-gadis-yang-cantik-sekali-dibuka.aup`
there are 6 separate recordings (arguably, di and buka should be separate).
Through careful prosody during recording AND careful during composition, it sounds quite natural.

To make it natural there are some criteria which must be filled:

1. **Position Prosody**. It's important but not the only one.
    Each unit must be spoken in each of 4 basic position prosodies to be flexible during synthesis.
    During recording, prosody must be annotated.
    During synthesis, based on prosody we choose the correct voice.
2. **Syllable Beat Timing**. Speech also has tempo, and there is a "measure" and also there is beat.
    Each syllable (or diphone) must fall _exactly_ in a beat of the proper measure or it'll sound weird.
    (This is obviously relaxed when on a gap like comma, period, or ellipsis.)
    By _exactly_ I mean while it's not frame-precise, a latency of 10ms won't be acceptable.
    During recording, beat must be annotated.
    Annotating the beat is best done not prescripted but by the actor, using keyboard, right on the beat.
    So the actor can be as expressive as they wish

It's possible for a set of words/synth-units, to make 4 records containing each variation.
However the recording would be weird, but it's OK.
So there would be exactly n × 4 words to read.

1. Ketika gadis yang, cantik. Sekali dibuka sekali, dibuka.
2. Cantik ketika gadis, yang. Dibuka sekali dibuka, sekali.
3. Yang cantik ketika, gadis.
4. Gadis yang cantik, ketika.

General algorithm is, you need to divide the words into groups of 4.
The permute them based on the following:

* group-of-4: A B C, D. B C D, A. C D A, B. D A B, C.
* group-of-3: A B C, A. B C A, B. C B A, B.
* group-of-2: A B A, B. B A B, A.
* group-of-1: A A A, A.

### Proof 2: Incomplete

Targets:
Ketika pintu di02-buka, tampak seorang gadis yang cantik sekali sedang ber-diri di0 depan pintu2.
Ketika gadis yang cantik sekali ber-diri di0 depan pintu2 di02-buka, tampak seorang sedang .

Sources:

## Fill-in-the-blanks and Alternated Recording

Similar to fill-in-the-blanks recording but for every other word.

For:

Selamat datang di terminal Dago. Kepada seluruh penumpang yang telah memiliki karcis,
dipersilakan memasuki area yang sudah disediakan. Terima kasih.

* Dago, penumpang, karcis, area
* Cihampelas, penduduk, KTP, gerbang
* Juanda, penerbang, sertifikat, landasan

You do:

Selamat _ di _ Dago. _ seluruh _ yang _ me- _ -ki _,
diper- _ -kan _ masuk _ area _ sudah _ sedia _. Terima _.

_ datang _ terminal _. Kepada _ penumpang _ telah _ milik _ karcis,
_ sila _ me- _ -ki _ yang _ di- _ -kan. _ kasih.

Verdict: doesn't sound natural. Very hard to get right "feel".
Need to be a **really** good voice actor to do this right, but the difficulty level is too hard.

Fill-in-the-blanks recording sound **much** better (but still slightly weird) than alternated.
Fill-in-the-blanks recording is even better/natural when you use soft-spoken/whispering or articulated voice.
Example: `/media/ceefour/passport/project_passport/lumen/speech/expressive/selamat-datang-di-terminal.aup`

## Soft Spoken / Gentle Whispering Voice (ASMR)

A good model for expressive speech is soft spoken speech, like what you find in YouTube videos of:
TheWaterwhispers, GentleWhispering, WhisperCrystal. (unfortunately some of them have audible background noise,
some perhaps intentional -brainwave thingy-, but fortunately most of them have plenty of noise-only parts
so you can Noise Remove it using Audacity.)

Just like Scarlett Johansson in "Her". (and that definitely worked because he fell in love with the OS,
so that's gonna be our number 1 priority speech style)

YouTube search queries:

* bedtime fairy tales
* soft spoken story

## Interjections Voice Library

Check "oldu canim" YouTube videos, and videos around "Wang Yuanji Voice & Sensual Moans".
(unfortunately it gets repetitive real quick)

## Emotions Library

See YouTube "30 emotions voice acting exercise". Very good and covers all bases.

Note that while simple emotions only contain one phase,
some emotions may have two or three phases.

1. Angry. 2 phases.
2. Anxious
3. Ashamed.
4. Bored.
5. Cautious.
6. Confident.
7. Confused. 4 phases.
8. Depressed
9. Disgusted. 2 phases.
10. Ecstatic. 3 phases.
11. Embarrassed.
12. Enraged. 2 phases.
13. Exhausted.
14. Frightened. 3 phases.
15. Frustrated. 2 phases.
16. Guilty. 2 phases.
17. Happy.
18. Hopeful. 2 phases.
19. Hysterical. 3 phases.
20. Jealous. 2 phases.
21. Lonely.
22. Lovestruck.
23. Mischievious.
24. Overwhelmed. 2 phases.
25. Sad.
26. Shocked.
27. Shy.
28. Smug.

Also:

* Anime Voice Acting by JemmaKuma
* "voice acting"
