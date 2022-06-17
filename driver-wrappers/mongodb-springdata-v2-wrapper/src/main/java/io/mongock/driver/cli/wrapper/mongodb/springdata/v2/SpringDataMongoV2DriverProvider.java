package io.mongock.driver.cli.wrapper.mongodb.springdata.v2;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.mongock.cli.util.CliConfiguration;
import io.mongock.cli.util.ConnectionDriverProvider;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.springdata.v2.SpringDataMongoV2Driver;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;

public class SpringDataMongoV2DriverProvider implements ConnectionDriverProvider {


    @Override
    public ConnectionDriver getDriver(CliConfiguration configuration) {


        MongoTemplate mongoTemplate = getMongoTemplate(configuration.getDatabaseUrl(), configuration.getDatabaseName());

        // Driver
        SpringDataMongoV2Driver driver = SpringDataMongoV2Driver.withDefaultLock(mongoTemplate);
        driver.enableTransaction();

        return driver;
    }

    /**
     * Main MongoTemplate for Mongock to work.
     */
    private static MongoTemplate getMongoTemplate(String connectionString, String dbName) {

        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, dbName);

        // Custom converters to map ZonedDateTime.
        MappingMongoConverter mongoMapping = (MappingMongoConverter) mongoTemplate.getConverter();
        mongoMapping.setCustomConversions(customConversions());
        mongoMapping.afterPropertiesSet();

        return mongoTemplate;
    }


    private static MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(DateToZonedDateTimeConverter.INSTANCE);
        converters.add(ZonedDateTimeToDateConverter.INSTANCE);
        return new MongoCustomConversions(converters);
    }

}
