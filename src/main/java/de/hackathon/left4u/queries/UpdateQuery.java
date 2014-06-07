package de.hackathon.left4u.queries;

import org.bson.types.ObjectId;

import com.google.common.base.Preconditions;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class UpdateQuery implements IQuery<WriteResult> {

	private final DBCollection collection;
	private final ObjectId _id;
	private final DBObject update;

	public UpdateQuery(final DBCollection collection, final String _id, final DBObject update) {

		Preconditions.checkArgument(collection != null, "collection == null");
		Preconditions.checkArgument(_id != null, "_id == null");
		Preconditions.checkArgument(update != null, "update == null");

		this.collection = collection;
		this._id = new ObjectId(_id);
		this.update = update;
	}

	public WriteResult execute() {

		final DBObject updateQuery = new BasicDBObject("_id", _id);

		return collection.update(updateQuery, update);
	}
}
