// @java.file.header

/*  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.gridgain.grid.cache.query;

import org.gridgain.grid.*;
import org.gridgain.grid.cache.*;
import org.gridgain.grid.lang.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Cache query with possible remote and local reducers. The execution sequence is
 * essentially identical to the one described in {@link GridCacheQuery} javadoc,
 * except that queried key-value pairs are given to an optional reducer
 * directly on the queried node and a single reduced value is returned back
 * to caller node. Then on the caller node, the collection of reduced values
 * is given to optionally provided local reducer. Based on whether local
 * reducer is provided or not, either a single value or a collection of
 * reduced values is returned to user.
 * <p>
 * Reduce queries are created from {@link GridCacheProjection} API via any
 * of the available {@code createReduceQuery(...)} methods.
 * <h1 class="header">Reduce Query Usage</h1>
 * Here is a query example which calculates average salary for all cached employees of
 * any given company based on the same example data mode described in {@link GridCacheQuery}
 * documentation.
 * <pre name="code" class="java">
 * GridCache&lt;Long, Person&gt; cache = G.grid().cache();
 *
 * // Calculate average of salary of all employees in some company.
 * GridCacheReduceQuery&lt;UUID, Person, GridTuple2&lt;Double, Integer&gt;, Double&gt; qry =
 *   cache.createReduceQuery(SQL, Person.class,
 *     "from Person, Organization where Person.orgId = Organization.id and lower(Organization.name) = lower(?)");
 *
 * // Set remote reducer to calculate sum of salaries and employee count on remote nodes.
 * qry.remoteReducer(new CO&lt;GridReducer&lt;Map.Entry&lt;Long, Person&gt;, GridTuple2&lt;Double, Integer&gt;&gt;&gt;() {
 *     private GridReducer&lt;Map.Entry&lt;Long, Person&gt;, GridTuple2&lt;Double, Integer&gt;&gt; rdc =
 *         new GridReducer&lt;Map.Entry&lt;Long, Person&gt;, GridTuple2&lt;Double, Integer&gt;&gt;() {
 *             private double sum;
 *             private int cnt;
 *
 *             &#64;Override public boolean collect(Map.Entry&lt;Long, Person&gt; e) {
 *                 sum += e.getValue().getSalary();
 *
 *                 cnt++;
 *
 *                 // Continue collecting.
 *                 return true;
 *             }
 *
 *             &#64;Override public GridTuple2&lt;Double, Integer&gt; apply() {
 *                 return new GridTuple2&lt;Double, Integer&gt;(sum, cnt);
 *             }
 *         };
 *
 *         &#64;Override public GridReducer&lt;Map.Entry&lt;Long, Person&gt;, GridTuple2&lt;Double, Integer&gt;&gt; apply() {
 *             return rdc;
 *         }
 *     });
 *
 * // Set local reducer to reduce totals from queried nodes into overall average.
 * qry.localReducer(new CO&lt;GridReducer&lt;GridTuple2&lt;Double, Integer&gt;, Double&gt;&gt;() {
 *     private GridReducer&lt;GridTuple2&lt;Double, Integer&gt;, Double&gt; rdc =
 *         new GridReducer&lt;GridTuple2&lt;Double, Integer&gt;, Double&gt;() {
 *             private double sum;
 *             private int cnt;
 *
 *             &#64;Override public boolean collect(GridTuple2&lt;Double, Integer&gt; e) {
 *                 sum += e.get1();
 *                 cnt += e.get2();
 *
 *                 // Continue collecting.
 *                 return true;
 *             }
 *
 *             &#64;Override public Double apply() {
 *                 return cnt == 0 ? 0 : sum / cnt;
 *             }
 *         };
 *
 *         &#64;Override public GridReducer&lt;GridTuple2&lt;Double, Integer&gt;, Double&gt; apply() {
 *             return rdc;
 *         }
 *     });
 *
 * // Query all nodes to find average salary of all GridGain employees.
 * double averageGridGainSalary = qry.with("GridGain").reduce(grid).get())
 *
 * // Query all nodes to find average salary of all employees working for "Other" company.
 * double averageOtherSalary = qry.with("Other").reduce(grid).get())
 * </pre>
 *
 * @param <K> Key type.
 * @param <V> Value type.
 * @param <R1> Remotely reduced type.
 * @param <R2> Locally reduced type.
 * @author @java.author
 * @version @java.version
 */
public interface GridCacheReduceQuery<K, V, R1, R2>
    extends GridCacheQueryBase<K, V, GridCacheReduceQuery<K, V, R1, R2>> {
    /**
     * Remote reducer for reduction of multiple queried values on queried nodes into one.
     * <p>
     * If reducer is set, then it is used for every query execution.
     *
     * @param reducer Remote reducer to use on queried nodes.
     * @return New query instance with remote reducer set.
     */
    public GridCacheReduceQuery<K, V, R1, R2> remoteReducer(GridReducer<Map.Entry<K, V>, R1> reducer);

    /**
     * Local reducer for reduction of multiple queried values returned from remote nodes into one.
     * <p>
     * If reducer is set, then it is used for every query execution.
     *
     * @param reducer Reducer to reduce query results returned from queried nodes.
     * @return New query instance with local reducer set.
     */
    public GridCacheReduceQuery<K, V, R1, R2> localReducer(GridReducer<R1, R2> reducer);

    /**
     * Optional query arguments that get passed to query SQL.
     *
     * @param args Optional query arguments.
     * @return This query with the passed in arguments preset.
     */
    public GridCacheReduceQuery<K, V, R1, R2> queryArguments(@Nullable Object... args);

    /**
     * Executes query on the given grid projection using remote and local reducers and
     * returns a future for the queried result.
     * <p>
     * Note that if the passed in grid projection is a local node, then query
     * will be executed locally without distribution to other nodes.
     * <p>
     * Also note that query state cannot be changed (clause, timeout etc.), except
     * arguments, if this method was called at least once.
     *
     * @return Future for the reduced query result.
     */
    public GridFuture<R2> reduce();

    /**
     * Executes query on the given grid projection using remote reducer and
     * returns a future for the queried result. The result is a collection of
     * reduced values returned from queried nodes.
     * <p>
     * Note that if the passed in grid projection is a local node, then query
     * will be executed locally without distribution to other nodes.
     * <p>
     * Also note that query state cannot be changed (clause, timeout etc.), except
     * arguments, if this method was called at least once.
     *
     * @return Future for the reduced query result.
     */
    public GridFuture<Collection<R1>> reduceRemote();
}
