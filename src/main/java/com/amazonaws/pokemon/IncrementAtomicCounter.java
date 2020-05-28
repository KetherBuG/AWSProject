package com.amazonaws.pokemon;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

public class IncrementAtomicCounter {

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

        Table table = dynamoDB.getTable("PokemonKetchemAll");

        int year = 2015;
        String title = "Charmander";

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("year", year, "title", title)
            .withUpdateExpression("set info.rating = info.rating + :val")
            .withValueMap(new ValueMap().withNumber(":val", 1)).withReturnValues(ReturnValue.UPDATED_NEW);

        try {
            System.out.println("Incrementing an atomic counter...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());

        }
        catch (Exception e) {
            System.err.println("Unable to update item: " + year + " " + title);
            System.err.println(e.getMessage());
        }
    }
}