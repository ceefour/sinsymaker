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
    int preStart;
    int preLength;
    int bodyStart;
    int bodyLength;
    int postStart;
    int postLength;

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
}
