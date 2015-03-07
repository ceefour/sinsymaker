package id.ac.itb.lumen.sinsymaker;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by ceefour on 07/03/15.
 */
public class LanguageTagDeserializer extends StdScalarDeserializer<Locale> {
    protected LanguageTagDeserializer() {
        super(Locale.class);
    }

    @Override
    public Locale deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return Locale.forLanguageTag(jp.getText());
    }
}
