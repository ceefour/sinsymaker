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
 * Created by ceefour on 06/03/15.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(property = "@type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes(@JsonSubTypes.Type(name = "Word", value = Word.class))
public class Word {

    public static enum Kind {
        /**
         * A proper language word, e.g. "saya".
         */
        LANGUAGE,
        /**
         * Name of a person, place, or entity, e.g. "Hendy Irawan".
         * While it can be marked in a language, it's language-indepedent enough to
         * be usable in any language context.
         */
        NAME,
        /**
         * Interjection or exclamation, e.g. "wow", "ah", etc.
         */
        INTERJECTION,
        /**
         * A sound or word that is spoken in conversation by one participant to signal to others that he/she has paused to think but has not yet finished speaking,
         * e.g. "uh", "er", "um", "huh", "erm".
         */
        FILLER,
        /**
         * A prefix, such as "ber-", "di-", "per-".
         * Phoneme assimilation might be desirable in some cases.
         */
        PREFIX,
        /**
         * A suffix, such as "-an", "-kan".
         * Phoneme assimilation might be desirable in some cases.
         */
        SUFFIX,
    }

    String id;
    String name;
    @JsonSerialize(using = LanguageTagSerializer.class)
    @JsonDeserialize(using = LanguageTagDeserializer.class)
    Locale language;
    Kind kind;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Trimmed, and several characters removed: ,"
     * TBD: () is retained for {@link Kind#INTERJECTION} like (hehe).
     * But should we just use "hehe" instead? Since in writing we'll just write I love you, hehe.
     * . is also retained for ellipsis.
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }
}
