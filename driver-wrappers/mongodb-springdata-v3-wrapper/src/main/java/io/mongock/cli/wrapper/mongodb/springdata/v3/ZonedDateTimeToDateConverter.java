package io.mongock.cli.wrapper.mongodb.springdata.v3;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.time.ZonedDateTime;
import java.util.Date;

@WritingConverter
public enum ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date> {
  INSTANCE;

  @Override
  public Date convert(ZonedDateTime source) {
    return source == null ? null : Date.from(source.toInstant());
  }
}
