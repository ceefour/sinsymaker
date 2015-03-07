package id.ac.itb.lumen.sinsymaker;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by ceefour on 07/03/15.
 */
public class LanguageTagSerializer extends StdScalarSerializer<Locale> {
    protected LanguageTagSerializer() {
        super(Locale.class);
    }

    @Override
    public void serialize(Locale value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeString(value.toLanguageTag());
    }
}
