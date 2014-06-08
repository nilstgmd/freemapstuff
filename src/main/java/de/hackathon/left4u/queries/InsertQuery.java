package de.hackathon.left4u.queries;

import com.google.common.base.Preconditions;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

/**
 * @author <a href="mailto:marhoffm@adobe.com">Marvin Hoffmann</a>
 */
public class InsertQuery implements IQuery<WriteResult> {

	private final DBCollection collection;
	private final DBObject insert;

	public InsertQuery(final DBCollection collection, final DBObject insert) {

		Preconditions.checkNotNull(collection != null, "collection == null");
		Preconditions.checkNotNull(insert != null, "insert == null");

		this.collection = collection;
		this.insert = insert;
	}

	public WriteResult execute() {

		return collection.insert(insert);
	}
}
