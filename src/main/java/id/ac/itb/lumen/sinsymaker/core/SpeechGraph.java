package id.ac.itb.lumen.sinsymaker.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * A graph that contains information about words, clauses, and vocal emotions/prosody;
 * but not audio/voice data.
 *
 * Created by ceefour on 06/03/15.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(property = "@type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes(@JsonSubTypes.Type(name = "Word", value = Word.class))
public class SpeechGraph {

    final Map<String, Word> words = new HashMap<>();
    final Map<String, Clause> clauses = new HashMap<>();
    final Map<String, Voice> voices = new HashMap<>();

    /**
     * Key is word ID.
     * @return
     */
    public Map<String, Word> getWords() {
        return words;
    }

    /**
     * Key is clause ID.
     * @return
     */
    public Map<String, Clause> getClauses() {
        return clauses;
    }

    /**
     * Key is voice ID.
     * @return
     */
    public Map<String, Voice> getVoices() {
        return voices;
    }
}
