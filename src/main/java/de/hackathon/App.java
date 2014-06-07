package de.hackathon;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

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

				final List<DBObject> results = new ArrayList<DBObject>();
				final DBCursor resultCursor = stuffCollection.find();

				while (resultCursor.hasNext()) {
					results.add(resultCursor.next());
				}

				return new BasicDBObject("results", results);
			}
		});

		Spark.post(new Route("/stuff/") {
			@Override
			public Object handle(final Request request, final Response response) {

				final DBObject newStuff = (DBObject) JSON.parse(request.body());
				final WriteResult result = stuffCollection.insert(newStuff);

				return result;
			}
		});

		Spark.put(new Route("/stuff/:id") {
			@Override
			public Object handle(final Request request, final Response response) {

				final DBObject updatedStuff = (DBObject) JSON.parse(request.body());
				final WriteResult result = stuffCollection.update(
						new BasicDBObject("_id", new ObjectId(request
								.params(":id"))), updatedStuff);

				return result;
			}
		});
	}
}
