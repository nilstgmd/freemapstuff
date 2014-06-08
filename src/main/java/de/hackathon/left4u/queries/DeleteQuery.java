package de.hackathon.left4u.queries;

import org.bson.types.ObjectId;

import com.google.common.base.Preconditions;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class DeleteQuery implements IQuery<WriteResult> {

	private final DBCollection collection;
	private final ObjectId id;

	public DeleteQuery(final DBCollection collection, final String id) {

		Preconditions.checkNotNull(collection != null, "collection == null");
		Preconditions.checkNotNull(id != null, "_id == null");

		this.collection = collection;
		this.id = new ObjectId(id);
	}

	public WriteResult execute() {

		final DBObject deleteQuery = new BasicDBObject("_id", id);

		return collection.remove(deleteQuery);
	}
}
