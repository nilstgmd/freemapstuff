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
import com.mongodb.BasicDBObjectBuilder;
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
				DBCursor resultCursor = null;
				//we check the attributes
				final String jsSortyBy = request.queryParams("sort");
				if (jsSortyBy != null && !jsSortyBy.equals(""))
				{
					if (jsSortyBy.equals("location"))
					{
						// we want to search a location.
						final Integer dLat = Integer.parseInt(request
								.queryParams("lat"));
						final Integer dLong = Integer.parseInt(request
								.queryParams("long"));

						final DBObject whereQuery = BasicDBObjectBuilder
								.start()
								.push("location")
								.push("$near")
								.add("$maxDistance", 5)
								.push("$geometry")
								.add("type", "Point")
								.add("coordinates",
										new Integer[] { dLat, dLong }).get();
						resultCursor = stuffCollection.find(whereQuery);
					}
					else if (jsSortyBy.equals("created"))
					{
						
						// we sort by the created date
						final BasicDBObject sortPredicate = new BasicDBObject();
						sortPredicate.put("date", -1);

						resultCursor = stuffCollection.find().sort(sortPredicate);
					}
				}
				else
				{
					resultCursor = stuffCollection.find();
				}

				while (resultCursor.hasNext()) {
					results.add(resultCursor.next());
				}

				return new BasicDBObject("results", results);
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
				final DBCursor cursor = stuffCollection.find(whereQuery);
				final DBObject result = cursor.next();
				return result;
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
		
		Spark.delete(new Route("/stuff/:id") {
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
				final WriteResult result = stuffCollection.remove(whereQuery);
				return result;
			}
		});
	}
}
