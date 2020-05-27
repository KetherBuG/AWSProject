package com.amazonaws.samples;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;

public class ReadItem {

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
        String title = "BOIS";

        GetItemSpec spec = new GetItemSpec().withPrimaryKey("year", year, "title", title);

        try {
            System.out.println("Attempting to read the item...");
            Item outcome = table.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome);

        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + year + " " + title);
            System.err.println(e.getMessage());
        }

    }
}