package com.modelmetrics.api.modelmetrics.converter;

import jakarta.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Component;

/** StringAttributeConverter. */
@Component
@RequiredArgsConstructor
public class StringAttributeConverter implements AttributeConverter<String, String> {

  private final StringEncryptor stringEncryptor;

  @Override
  public String convertToDatabaseColumn(String attribute) {
    return attribute != null ? stringEncryptor.encrypt(attribute) : null;
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    return dbData != null ? stringEncryptor.decrypt(dbData) : null;
  }
}
