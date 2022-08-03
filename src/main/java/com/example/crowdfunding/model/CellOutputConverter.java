package com.example.crowdfunding.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nervos.ckb.type.CellOutput;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CellOutputConverter implements AttributeConverter<CellOutput, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(CellOutput meta) {
        try {
            return objectMapper.writeValueAsString(meta);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CellOutput convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, CellOutput.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
