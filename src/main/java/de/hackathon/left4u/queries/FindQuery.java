package de.hackathon.left4u.queries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class FindQuery implements IQuery<DBObject> {

	private final DBCollection collection;
	private final boolean getLatestOnly;
	private final Optional<Double> lat;
	private final Optional<Double> lon;
	private final Optional<Double> distance;
	private final Optional<List<String>> tags;

	public FindQuery(final DBCollection collection, final boolean getLatestOnly) {

		Preconditions.checkArgument(collection != null, "collection == null");

		this.collection = collection;
		this.getLatestOnly = getLatestOnly;

		this.lat = Optional.absent();
		this.lon = Optional.absent();
		this.distance = Optional.absent();
		this.tags = Optional.absent();
	}

	public FindQuery(final DBCollection collection, final String lat,
			final String lon, final String distance) {

		Preconditions.checkArgument(collection != null, "collection == null");
		Preconditions.checkArgument(lat != null, "lat == null");
		Preconditions.checkArgument(lon != null, "lon == null");
		Preconditions.checkArgument(distance != null, "distance == null");

		this.collection = collection;
		this.lat = Optional.of(Double.parseDouble(lat));
		this.lon = Optional.of(Double.parseDouble(lon));
		this.distance = Optional.of(Double.parseDouble(distance));

		this.getLatestOnly = false;
		this.tags = Optional.absent();
	}

	public FindQuery(final DBCollection collection, final List<String> tags) {

		Preconditions.checkArgument(collection != null, "collection == null");
		Preconditions.checkArgument(tags != null, "tags == null");

		this.collection = collection;
		this.tags = Optional.of(tags);

		this.getLatestOnly = false;
		this.lat = Optional.absent();
		this.lon = Optional.absent();
		this.distance = Optional.absent();
	}

	public DBObject execute() {

		DBCursor resultsCursor;

		if (lat.isPresent() && lon.isPresent() && distance.isPresent()) {

			final DBObject findByLocationQuery = BasicDBObjectBuilder.start()
					.push("location")
					.push("$near")
						.add("$maxDistance", distance.get())
						.push("$geometry")
							.add("type", "Point")
							.add("coordinates", new Double[] { lat.get(), lon.get() }).get();

			resultsCursor = collection.find(findByLocationQuery);
		}
		else if (getLatestOnly) {

			final Date now = new Date();
			final Date aWeekAgo = new Date(now.getTime() - 1000 * 60 * 60 * 24
					* 7);

			final DBObject greaterThen = BasicDBObjectBuilder.start()
					.push("created").add("$gt", aWeekAgo).get();
			final DBObject newestFirst = new BasicDBObject("created", -1);

			resultsCursor = collection.find(greaterThen).sort(newestFirst);
		}
		else if (tags.isPresent()) {

			final DBObject filterByTags = BasicDBObjectBuilder.start()
					.push("tags").add("$in", tags.get()).get();
			
			resultsCursor = collection.find(filterByTags);
		}
		else {

			resultsCursor = collection.find();
		}

		final List<DBObject> results = new ArrayList<DBObject>();
		while (resultsCursor.hasNext()) {
			results.add(resultsCursor.next());
		}

		final DBObject result = new BasicDBObject("results", results);

		return result;
	}
}
