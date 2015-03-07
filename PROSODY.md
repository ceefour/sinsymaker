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

1. Pintu1 mulai dibuka | Gadis mulai1 dibuka
2. Ketika pintu2 dibuka | Ketika gadis2 yang
3. di depan pintu3, dia...  | di depan gadis3, dia...
4. Ada di depan pintu4. | Ada di depan gadis4.

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



### Proof 2: Incomplete

Targets:
Ketika pintu di02-buka, tampak seorang gadis yang cantik sekali sedang ber-diri di0 depan pintu2.
Ketika gadis yang cantik sekali ber-diri di0 depan pintu2 di02-buka, tampak seorang sedang .

Sources:

