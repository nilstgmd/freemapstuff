package de.hackathon.left4u.queries;

import org.bson.types.ObjectId;

import com.google.common.base.Preconditions;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

/**
 * @author <a href="mailto:marhoffm@adobe.com">Marvin Hoffmann</a>
 */
public class UpdateQuery implements IQuery<WriteResult> {

	private final DBCollection collection;
	private final ObjectId id;
	private final DBObject update;

	public UpdateQuery(final DBCollection collection, final String id, final DBObject update) {

		Preconditions.checkNotNull(collection != null, "collection == null");
		Preconditions.checkNotNull(id != null, "_id == null");
		Preconditions.checkNotNull(update != null, "update == null");

		this.collection = collection;
		this.id = new ObjectId(id);
		this.update = update;
	}

	public WriteResult execute() {

		final DBObject updateQuery = new BasicDBObject("_id", id);

		return collection.update(updateQuery, update);
	}
}
