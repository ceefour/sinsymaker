package id.ac.itb.lumen.sinsymaker.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import id.ac.itb.lumen.sinsymaker.LanguageTagDeserializer;
import id.ac.itb.lumen.sinsymaker.LanguageTagSerializer;

import java.util.Locale;

/**
 * A voice sample of a clause, word, or interjection.
 * Created by ceefour on 06/03/15.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(property = "@type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes(@JsonSubTypes.Type(name = "Voice", value = Voice.class))
public class Voice {

    public static enum Kind {
        CLAUSE, WORD;
    }

    public static enum RelativePosition {
        /**
         * Begins an entire sentence.
         */
        BEGIN_SENTENCE,
        /**
         * Begin of a clause but not sentence, for example "that" in "I want to let you know, that I'm happy".
         */
        BEGIN_CLAUSE,
        /**
         * Inside a running clause.
         */
        IN_CLAUSE,
        /**
         * End a clause but not the sentence, for example "know" in "I want to let you know, that I'm happy".
         */
        END_CLAUSE,
        /**
         * End of sentence before a period prosody, ellipsis prosody, question prosody, or exclamation prosody.
         */
        END_SENTENCE,
        /**
         * For example "Yes", "No".
         */
        INDEPENDENT,
        /**
         * An independent word leading to something else, e.g. "Hi", "Well".
         */
        LEADING,
        /**
         * An independent word trailing from something else, e.g. "love" in "What I feel for you is... love"
         */
        TRAILING
    }

    String id;
    String name;
    Kind kind;
    String clauseId;
    String wordId;
    String speakerId;
    @JsonSerialize(using = LanguageTagSerializer.class)
    @JsonDeserialize(using = LanguageTagDeserializer.class)
    Locale language;
    String prevWordId;
    String nextWordId;
    RelativePosition relativePosition;

    int length;
    int preStart;
    int preLength;
    int bodyStart;
    int bodyLength;
    int postStart;
    int postLength;
    float pitch;
    float rms;

    /**
     * e.g. {@code kamu_pasti_kedinginan}
     * @return
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    /**
     * Only used for {@link Kind#CLAUSE}.
     * @return
     */
    public String getClauseId() {
        return clauseId;
    }

    public void setClauseId(String clauseId) {
        this.clauseId = clauseId;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    /**
     * Only used for {@link Kind#WORD}.
     * @return
     */
    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public String getSpeakerId() {
        return speakerId;
    }

    public void setSpeakerId(String speakerId) {
        this.speakerId = speakerId;
    }

    public String getPrevWordId() {
        return prevWordId;
    }

    public void setPrevWordId(String prevWordId) {
        this.prevWordId = prevWordId;
    }

    public String getNextWordId() {
        return nextWordId;
    }

    public void setNextWordId(String nextWordId) {
        this.nextWordId = nextWordId;
    }

    public RelativePosition getRelativePosition() {
        return relativePosition;
    }

    public void setRelativePosition(RelativePosition relativePosition) {
        this.relativePosition = relativePosition;
    }

    /**
     * Total length in frames, including pre and post.
     * @return
     */
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getPreStart() {
        return preStart;
    }

    public void setPreStart(int preStart) {
        this.preStart = preStart;
    }

    public int getPreLength() {
        return preLength;
    }

    public void setPreLength(int preLength) {
        this.preLength = preLength;
    }

    public int getBodyStart() {
        return bodyStart;
    }

    public void setBodyStart(int bodyStart) {
        this.bodyStart = bodyStart;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public int getPostStart() {
        return postStart;
    }

    public void setPostStart(int postStart) {
        this.postStart = postStart;
    }

    public int getPostLength() {
        return postLength;
    }

    public void setPostLength(int postLength) {
        this.postLength = postLength;
    }

    /**
     * {@link net.beadsproject.beads.ugens.RMS} (root-mean-square) power in sample-float (always positive).
     * @return
     */
    public float getRms() {
        return rms;
    }

    public void setRms(float rms) {
        this.rms = rms;
    }

    /**
     * Average pitch of this voice in Hz.
     * @return
     */
    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
