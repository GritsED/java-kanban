package server.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (Objects.nonNull(value)) {
            out.value(value.format(dtf));
        } else {
            out.value("null");
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        LocalDateTime time;
        try {
            time = LocalDateTime.parse(in.nextString(), dtf);
        } catch (DateTimeParseException e) {
            time = null;
        }
        return time;
    }
}
