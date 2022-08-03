package com.example.crowdfunding.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nervos.ckb.type.OutPoint;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class OutPointConverter implements AttributeConverter<OutPoint, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(OutPoint meta) {
        try {
            return objectMapper.writeValueAsString(meta);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OutPoint convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, OutPoint.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
