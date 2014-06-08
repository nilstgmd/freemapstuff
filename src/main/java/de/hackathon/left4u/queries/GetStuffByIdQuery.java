package de.hackathon.left4u.queries;

import org.bson.types.ObjectId;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * @author <a href="mailto:marhoffm@adobe.com">Marvin Hoffmann</a>
 */
public class GetStuffByIdQuery implements IQuery<DBObject> {

	private final DBCollection collection;
	private final Optional<ObjectId> id;

	public GetStuffByIdQuery(final DBCollection collection, final String id) {

		Preconditions.checkNotNull(collection != null, "collection == null");
		Preconditions.checkNotNull(id != null, "_id == null");

		this.collection = collection;
		this.id = Optional.of(new ObjectId(id));
	}

	public DBObject execute() {

		final DBObject findQuery = new BasicDBObject();

		if (id.isPresent()) {
			findQuery.put("_id", id.get());
		}

		return collection.findOne(findQuery);
	}
}
