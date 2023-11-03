package model;

import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

public class DynamoDBTable {

  public static String createTable(DynamoDbClient ddb, String tableName) {
    DynamoDbWaiter dbWaiter = ddb.waiter();

    try {
      DescribeTableRequest tableRequest = DescribeTableRequest.builder()
          .tableName(tableName)
          .build();

      try {
        DescribeTableResponse tableResponse = ddb.describeTable(tableRequest);
        TableStatus tableStatus = tableResponse.table().tableStatus();
        return tableStatus.toString();
      } catch (ResourceNotFoundException e) {
        // Table doesn't exist, create a new one
        CreateTableRequest request = CreateTableRequest.builder()
            .attributeDefinitions(
                AttributeDefinition.builder()
                    .attributeName("albumID")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName("profile")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName("image")
                    .attributeType(ScalarAttributeType.B)
                    .build()
            )
            .keySchema(KeySchemaElement.builder()
                .attributeName("albumID")
                .keyType(KeyType.HASH)
                .build())
            .provisionedThroughput(ProvisionedThroughput.builder()
                .readCapacityUnits(5L)
                .writeCapacityUnits(5L)
                .build())
            .tableName(tableName)
            .build();

        CreateTableResponse response = ddb.createTable(request);

        // Wait until the Amazon DynamoDB table is created.
        WaiterResponse<DescribeTableResponse> waiterResponse = dbWaiter.waitUntilTableExists(tableRequest);
        waiterResponse.matched().response().ifPresent(System.out::println);
        return response.tableDescription().tableName();
      }
    } catch (DynamoDbException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
    return "";
  }

  public static boolean doesTableExist(DynamoDbClient ddb, String tableName) {
    DescribeTableRequest request = DescribeTableRequest.builder()
        .tableName(tableName)
        .build();

    try {
      ddb.describeTable(request);
      // If the table exists, describeTable does not throw an exception.
      return true;
    } catch (ResourceNotFoundException e) {
      // The table does not exist.
      return false;
    } catch (DynamoDbException e) {
      // Handle other exceptions such as internal server error, etc.
      System.err.println(e.getMessage());
      throw e;
    }
  }
}
