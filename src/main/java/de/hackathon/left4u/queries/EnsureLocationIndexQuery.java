package de.hackathon.left4u.queries;

import com.google.common.base.Preconditions;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * @author <a href="mailto:marhoffm@adobe.com">Marvin Hoffmann</a>
 */
public class EnsureLocationIndexQuery implements IQuery<Void> {

	private final DBCollection collection;

	public EnsureLocationIndexQuery(final DBCollection collection) {

		Preconditions.checkNotNull(collection != null, "collection == null");

		this.collection = collection;
	}

	public Void execute() {

		final DBObject locationIndex = new BasicDBObject("location", "2dsphere");
		collection.createIndex(locationIndex);
		return null;
	}
}
