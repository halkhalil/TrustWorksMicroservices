package dk.trustworks.framework.network;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Created by hans on 15/11/2016.
 */
public class CustomDateSerializer extends JsonSerializer<LocalDate> {

    private static DateTimeFormatter formatter =
            DateTimeFormat.forPattern("dd-MM-yyyy");

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider arg2) throws IOException {
        gen.writeString(value.toString("dd-MM-yyyy"));
    }
}