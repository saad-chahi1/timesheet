package com.example.timesheetapp.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Time;

public class SqlTimeDeserializer extends JsonDeserializer<Time> {
    @Override
    public Time deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        if (jp.getValueAsString().length() == 0) {
            return Time.valueOf("00:00:00");
        } else if (jp.getValueAsString().length() == 8) {
            return Time.valueOf(jp.getValueAsString());
        } else if (jp.getValueAsString().length() == 5) {
            return Time.valueOf(jp.getValueAsString() + ":00");
        }else{
            return Time.valueOf(jp.getValueAsString() + ":00");
        }
    }



}
