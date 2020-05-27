package com.amazonaws.samples;

import java.util.Arrays;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

public class CreateMovieTable {

    public static void main(String[] args) throws Exception {
    	  ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
          try {
              credentialsProvider.getCredentials();
          } catch (Exception e) {
              throw new AmazonClientException(
                      "Cannot load the credentials from the credential profiles file. " +
                      "Please make sure that your credentials file is at the correct " +
                      "location (/Users/johnmortensen/.aws/credentials), and is in valid format.",
                      e);
          }

       /* AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
            .build();*/
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            	.withCredentials(credentialsProvider)
                .withRegion("us-west-2")
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        String tableName = "AzMovies";

        try {
            System.out.println("Attempting to create table; please wait...");
            Table table = dynamoDB.createTable(tableName,
                Arrays.asList(new KeySchemaElement("year", KeyType.HASH), // Partition
                                                                          // key
                    new KeySchemaElement("title", KeyType.RANGE)), // Sort key
                Arrays.asList(new AttributeDefinition("year", ScalarAttributeType.N),
                    new AttributeDefinition("title", ScalarAttributeType.S)),
                new ProvisionedThroughput(10L, 10L));
            table.waitForActive();
            System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());

        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }

    }
}