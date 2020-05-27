package com.amazonaws.samples;

import java.util.HashMap;
import java.util.Iterator;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

public class Query {

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

        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#yr", "year");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":yyyy", 1985);

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#yr = :yyyy").withNameMap(nameMap)
            .withValueMap(valueMap);

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        try {
            System.out.println("Movies from 1985");
            items = table.query(querySpec);

            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                System.out.println(item.getNumber("year") + ": " + item.getString("title"));
            }

        }
        catch (Exception e) {
            System.err.println("Unable to query movies from 1985");
            System.err.println(e.getMessage());
        }

        valueMap.put(":yyyy", 1992);
        valueMap.put(":letter1", "A");
        valueMap.put(":letter2", "L");

        querySpec.withProjectionExpression("#yr, title, info.genres, info.actors[0]")
            .withKeyConditionExpression("#yr = :yyyy and title between :letter1 and :letter2").withNameMap(nameMap)
            .withValueMap(valueMap);

        try {
            System.out.println("Movies from 1992 - titles A-L, with genres and lead actor");
            items = table.query(querySpec);

            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                System.out.println(item.getNumber("year") + ": " + item.getString("title") + " " + item.getMap("info"));
            }

        }
        catch (Exception e) {
            System.err.println("Unable to query movies from 1992:");
            System.err.println(e.getMessage());
        }
    }
}
