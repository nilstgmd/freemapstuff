package de.hackathon.left4u.queries;

import org.bson.types.ObjectId;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class FindOneQuery implements IQuery<DBObject> {

	private final DBCollection collection;
	private final Optional<ObjectId> _id;

	public FindOneQuery(final DBCollection collection, final String _id) {

		Preconditions.checkArgument(collection != null, "collection == null");
		Preconditions.checkArgument(_id != null, "_id == null");

		this.collection = collection;
		this._id = Optional.of(new ObjectId(_id));
	}

	public DBObject execute() {

		final DBObject findQuery = new BasicDBObject();

		if (_id.isPresent()) {
			findQuery.put("_id", _id.get());
		}

		return collection.findOne(findQuery);
	}
}
