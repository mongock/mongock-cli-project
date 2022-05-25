package io.mongock.driver.cli.wrapper.mongodb.springdata.v3;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.driver.ConnectionDriverProvider;
import io.mongock.driver.mongodb.springdata.v3.SpringDataMongoV3Driver;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class SpringDataMongoV3DriverProvider implements ConnectionDriverProvider {

    public final static String MONGODB_CONNECTION_STRING = "mongodb://localhost:27017/";
    public final static String MONGODB_MAIN_DB_NAME = "test";


    @Override
    public ConnectionDriver getDriver() {


        MongoTemplate mongoTemplate = getMongoTemplate();

        // Driver
        SpringDataMongoV3Driver driver = SpringDataMongoV3Driver.withDefaultLock(mongoTemplate);
        driver.enableTransaction();

        return driver;
    }

    /**
     * Main MongoTemplate for Mongock to work.
     */
    private static MongoTemplate getMongoTemplate() {

        MongoClient mongoClient = MongoClients.create(MONGODB_CONNECTION_STRING);
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, MONGODB_MAIN_DB_NAME);

        // Custom converters to map ZonedDateTime.
        MappingMongoConverter mongoMapping = (MappingMongoConverter) mongoTemplate.getConverter();
        mongoMapping.setCustomConversions(customConversions());
        mongoMapping.afterPropertiesSet();

        return mongoTemplate;
    }


    private static MongoDatabase getDatabase() {
        MongoClient mongoClient = buildMongoClientWithCodecs(MONGODB_CONNECTION_STRING);
        return mongoClient.getDatabase("secondaryDb");
    }


    private static MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(DateToZonedDateTimeConverter.INSTANCE);
        converters.add(ZonedDateTimeToDateConverter.INSTANCE);
        return new MongoCustomConversions(converters);
    }

    /**
     * Helper to create MongoClients customized including Codecs
     */
    private static MongoClient buildMongoClientWithCodecs(String connectionString) {

        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        return MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .codecRegistry(codecRegistry)
                .build());
    }
}
