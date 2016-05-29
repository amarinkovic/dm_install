package pro.documentum.persistence.common.query;

import java.util.Collection;

import org.datanucleus.ExecutionContext;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IDocumentumQuery {

    ExecutionContext getExecutionContext();

    Collection getCandidateCollection();

    boolean evaluateInMemory();

    String getSingleStringQuery();

}
