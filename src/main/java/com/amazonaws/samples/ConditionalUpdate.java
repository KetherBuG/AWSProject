package com.amazonaws.samples;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

public class ConditionalUpdate {

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

        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
           //Where the condition is defined
        		.withPrimaryKey(new PrimaryKey("year", year, "title", title)).withUpdateExpression("remove info.actors[0]")
            .withConditionExpression("size(info.actors) > :num").withValueMap(new ValueMap().withNumber(":num", 3))
            .withReturnValues(ReturnValue.UPDATED_NEW);

        // Conditional update (we expect this to fail)
        try {
            System.out.println("Attempting a conditional update...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());

        }
        catch (Exception e) {
            System.err.println("Unable to update item: " + year + " " + title);
            System.err.println(e.getMessage());
        }
    }
}