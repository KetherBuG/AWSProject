package com.amazonaws.samples;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

public class RemoveItem {

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

        Table table = dynamoDB.getTable("AzMovies");

        int year = 2015;
        //intentionally opt out
        String title = "";

        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
            .withPrimaryKey(new PrimaryKey("year", year, "title", title)).withConditionExpression("info.rating <= :val")
            .withValueMap(new ValueMap().withNumber(":val", 5));

        // Conditional delete (we expect this to fail)

        try {
            System.out.println("Attempting a conditional delete...");
            table.deleteItem(deleteItemSpec);
            System.out.println("DeleteItem succeeded");
        }
        catch (Exception e) {
            System.err.println("Unable to delete item: " + year + " " + title);
            System.err.println(e.getMessage());
        }
    }
}