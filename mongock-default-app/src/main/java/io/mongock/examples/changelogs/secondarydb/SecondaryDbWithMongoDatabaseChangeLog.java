package io.mongock.examples.changelogs.secondarydb;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import io.changock.migration.api.annotations.NonLockGuarded;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.examples.product.Product;
import org.bson.Document;

import javax.inject.Named;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static io.mongock.examples.MongockCliDefaultApp.PRODUCTS_COLLECTION_NAME;

@ChangeUnit(id="secondarydb-with-mongodatabase", order = "5", author="mongock")
public class SecondaryDbWithMongoDatabaseChangeLog {
  
  @Execution
  public void execution(ClientSession clientSession, MongoDatabase mongoDatabase, @Named("secondaryDb") @NonLockGuarded MongoDatabase secondaryDb) {

    // To show an example of reading secondary database and writing in main database, we
    // are going to migrate Products from secondaryDb (with MongoDatabase) to main db (with MongockTemplate).
    List<Product> products = StreamSupport.stream(secondaryDb.getCollection(PRODUCTS_COLLECTION_NAME, Product.class).find().spliterator(), false)
                                          .collect(Collectors.toList());
    
    // We have the read products, so now we'll insert them into the main database.
    mongoDatabase.getCollection(PRODUCTS_COLLECTION_NAME, Product.class).insertMany(clientSession, products);
  }

  @RollbackExecution
  public void rollbackExecution(ClientSession clientSession, MongoDatabase mongoDatabase, @Named("secondaryDb") @NonLockGuarded MongoDatabase secondaryDb) {
    
    mongoDatabase.getCollection("productsWithMongoDatabase").deleteMany(clientSession, new Document());
  }
}
