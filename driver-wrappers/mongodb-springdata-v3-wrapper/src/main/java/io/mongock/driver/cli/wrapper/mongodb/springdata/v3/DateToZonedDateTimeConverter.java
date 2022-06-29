package io.mongock.driver.cli.wrapper.mongodb.springdata.v3;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@ReadingConverter
public enum DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {
  INSTANCE;

  @Override
  public ZonedDateTime convert(Date source) {
    return source == null ? null : ZonedDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());

  }
}
