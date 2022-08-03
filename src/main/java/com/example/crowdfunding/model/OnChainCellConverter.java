package com.example.crowdfunding.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class OnChainCellConverter implements AttributeConverter<OnChainCell, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(OnChainCell meta) {
        try {
            return objectMapper.writeValueAsString(meta);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OnChainCell convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, OnChainCell.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
