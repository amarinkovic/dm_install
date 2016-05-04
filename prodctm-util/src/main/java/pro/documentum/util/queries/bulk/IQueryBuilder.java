package pro.documentum.util.queries.bulk;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IQueryBuilder<T> {

    String buildQuery(T param);

}
