package de.hackathon.left4u;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import de.hackathon.left4u.queries.BrowseStuffQuery;
import de.hackathon.left4u.queries.DeleteQuery;
import de.hackathon.left4u.queries.EnsureLocationIndexQuery;
import de.hackathon.left4u.queries.GetStuffByIdQuery;
import de.hackathon.left4u.queries.InsertQuery;
import de.hackathon.left4u.queries.UpdateQuery;

/**
 * Left4u server application
 * 
 * @author <a href="https://github.com/Age15990">Ann-Katrin Arning</a>
 * @author <a href="https://github.com/Desi252">Desiree Özgün</a>
 * @author <a href="mailto:marhoffm@adobe.com">Marvin Hoffmann</a>
 */
public class App {
	public static void main(final String[] args) throws UnknownHostException {
		final MongoClient mongo = new MongoClient();
		final DB mongoDb = mongo.getDB("left4u");
		final boolean firstStart = !mongoDb.collectionExists("stuff");
		final DBCollection stuffCollection = mongoDb.getCollection("stuff");

		if (firstStart) {
			final EnsureLocationIndexQuery ensureLocationIndexQuery = new EnsureLocationIndexQuery(
					stuffCollection);
			ensureLocationIndexQuery.execute();
		}

		Spark.get(new Route("/stuff/") {
			@Override
			public Object handle(final Request request, final Response response) {
				
				DBObject result;
				final String sortType = request.queryParams("sort");
				final String tags = request.queryParams("filter");

				if (sortType != null) {
					if (sortType.equals("location")) {
						
						final String lat = request.queryParams("lat");
						final String lon = request.queryParams("long");
						final String distance = "10";

						final BrowseStuffQuery findQuery = new BrowseStuffQuery(
								stuffCollection, lat, lon, distance);

						result = findQuery.execute();
					}
					else if (sortType.equals("created")) {
						
						final BrowseStuffQuery findQuery = new BrowseStuffQuery(stuffCollection, true);

						result = findQuery.execute();
					}
					else {
						throw new IllegalArgumentException("Unsupported sort parameter");
					}
				}
				else if(tags != null) {
					
					final List<String> tagsList = Arrays.asList(tags.split(","));
					final BrowseStuffQuery findQuery = new BrowseStuffQuery(stuffCollection, tagsList);
					
					result = findQuery.execute();
				}
				else {
					
					final BrowseStuffQuery findQuery = new BrowseStuffQuery(stuffCollection, false);
					
					result = findQuery.execute();
				}

				return result;
			}
		});
		
		Spark.get(new Route("/stuff/:id") {
			@Override
			public Object handle(final Request request, final Response response) {

				final String id = request.params(":id");
				final GetStuffByIdQuery findQuery = new GetStuffByIdQuery(stuffCollection, id);

				return findQuery.execute();
			}
		});

		Spark.post(new Route("/stuff/") {
			@Override
			public Object handle(final Request request, final Response response) {

			    final ObjectId id = new ObjectId();
				final DBObject insert = (DBObject) JSON.parse(request.body());
				insert.put("_id", id);
				final InsertQuery insertQuery = new InsertQuery(
						stuffCollection, insert);

				response.header("Location", id.toString());
				
				return insertQuery.execute().getN();
			}
		});

		Spark.put(new Route("/stuff/:id") {
			@Override
			public Object handle(final Request request, final Response response) {

				final String id = request.params(":id");
				final DBObject update = (DBObject) JSON.parse(request.body());
				final UpdateQuery putQuery = new UpdateQuery(stuffCollection, id, update);
				
				return putQuery.execute().getN();
			}
		});
		
		Spark.delete(new Route("/stuff/:id") {
			@Override
			public Object handle(final Request request, final Response response) {
				
				final String id = request.params(":id");
				final DeleteQuery deleteQuery = new DeleteQuery(stuffCollection, id);

				return deleteQuery.execute().getN();
			}
		});
	}
}
