package id.ac.itb.lumen.sinsymaker.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import id.ac.itb.lumen.sinsymaker.LanguageTagDeserializer;
import id.ac.itb.lumen.sinsymaker.LanguageTagSerializer;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by ceefour on 06/03/15.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(property = "@type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes(@JsonSubTypes.Type(name = "Clause", value = Clause.class))
public class Clause {

    String id;
    String name;
    @JsonSerialize(using = LanguageTagSerializer.class)
    @JsonDeserialize(using = LanguageTagDeserializer.class)
    Locale language;
    Set<String> wordIds = new HashSet<>();

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

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    /**
     * Unique words in this clause, in no particular order.
     * @return
     */
    public Set<String> getWordIds() {
        return wordIds;
    }
}
