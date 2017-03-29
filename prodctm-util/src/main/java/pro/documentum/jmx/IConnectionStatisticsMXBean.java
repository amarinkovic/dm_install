package pro.documentum.jmx;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IConnectionStatisticsMXBean {

    int getMaximumConnectionsCount();

    int getUnusedConnectionsCount();

    int getUsedConnectionsCount();

    int getTransitionConnectionsCount();

}
