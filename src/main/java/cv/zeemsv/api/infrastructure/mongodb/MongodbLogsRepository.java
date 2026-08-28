package cv.zeemsv.api.infrastructure.mongodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.PropertyAccessorFactory;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;

public abstract class MongodbLogsRepository<E> {
    private final MongoTemplate mongoTemplate;
    private String collectionName;

    protected MongodbLogsRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    protected void setCollection(String collectionName) {
        if (!StringUtils.hasText(collectionName)) {
            throw new IllegalArgumentException("The property collectionName is null or empty.");
        }
        this.collectionName = collectionName.trim();
    }

    protected List<ObjectId> saveLogs(List<E> listOfLogs) {
        if (listOfLogs == null || listOfLogs.isEmpty()) {
            throw new IllegalArgumentException("listOfLogs param is empty.");
        }

        Collection<E> insertedLogs = mongoTemplate.insert(listOfLogs, collectionName);

        return insertedLogs.stream()
            .map(this::getObjectId)
            .filter(id -> id != null)
            .toList();
    }

    protected ArrayList<E> loadLogs(int limit, Bson filter, Class<E> aClass) {
        return loadLogs(limit, filter, null, aClass);
    }

    protected ArrayList<E> loadLogs(int limit, Bson filter, Bson sort, Class<E> aClass) {
        int safeLimit = limit > 0 ? limit : 50;
        Bson safeFilter = filter == null ? new Document() : filter;
        ArrayList<E> result = new ArrayList<>();
        var findIterable = mongoTemplate.getCollection(collectionName).find(safeFilter);
        if (sort != null) {
            findIterable.sort(sort);
        }
        findIterable.limit(safeLimit)
            .map(document -> mongoTemplate.getConverter().read(aClass, document))
            .into(result);
        return result;
    }

    protected E findById(ObjectId id, Class<E> aClass) {
        if (id == null) {
            return null;
        }
        return mongoTemplate.findById(id, aClass, collectionName);
    }

    private ObjectId getObjectId(E log) {
        Object id = PropertyAccessorFactory.forBeanPropertyAccess(log).getPropertyValue("id");
        return id instanceof ObjectId objectId ? objectId : null;
    }
}
