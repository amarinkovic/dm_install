package pro.documentum.persistence.common.query;

import java.util.Collection;

import org.datanucleus.metadata.AbstractClassMetaData;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IDocumentumQuery<R> {

    Collection<R> getCandidateCollection();

    DQLQueryCompilation getDatastoreCompilation();

    boolean evaluateInMemory();

    String getSingleStringQuery();

    AbstractClassMetaData getCandidateMetaData();

    String getCandidateAlias();

}
