package de.hackathon;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(final String[] args) throws UnknownHostException {
		final MongoClient mongo = new MongoClient();
		final DB mongoDb = mongo.getDB("freestuffmap");
		final DBCollection stuffCollection = mongoDb.getCollection("stuff");

		Spark.get(new Route("/stuff/") {
			@Override
			public Object handle(final Request request, final Response response) {
				final DBObject result = stuffCollection.findOne();
				return result;
			}
		});
		
		Spark.get(new Route("/stuff/:id") {
			@Override
			public Object handle(final Request request, final Response response) {
				final String jsId = request.params(":id");
				DBObject whereQuery = null;
				if (jsId != null && !jsId.equals(""))
				{
					whereQuery = new BasicDBObject();
					final ObjectId jId = new ObjectId(jsId);
					whereQuery.put("_id", jId);
				}
				DBCursor cursor = stuffCollection.find(whereQuery);
				final DBObject result = cursor.next();
				return result;
			}
		});

		Spark.post(new Route("/stuff/") {
			@Override
			public Object handle(final Request request, final Response response) {

				return "Hello World!";
			}
		});
	}
}
