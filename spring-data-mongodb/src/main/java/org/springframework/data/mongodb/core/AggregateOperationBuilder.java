/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.mongodb.core;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.util.CloseableIterator;

/**
 * @author Christoph Strobl
 * @since 2.0
 */
public interface AggregateOperationBuilder {

	/**
	 * Start creating an aggregation operation that returns results mapped to the given domain type. <br />
	 * Use {@link org.springframework.data.mongodb.core.aggregation.TypedAggregation} to specify a potentially different
	 * input type for he aggreation.
	 *
	 * @param aggregate must not be {@literal null}.
	 * @param <T>
	 * @return
	 */
	public <T> AggregationOperation<T> aggregateAndReturn(Class<T> domainType);

	/**
	 * Collection override (Optional).
	 *
	 * @param <T>
	 * @author Christoph Strobl
	 * @since 2.0
	 */
	interface InCollection<T> {

		/**
		 * Explicitly set the name of the collection to perform the query on. <br />
		 * Just skip this step to use the default collection derived from the domain type.
		 *
		 * @param collection must not be {@literal null} nor {@literal empty}.
		 * @return
		 */
		DoAggregate<T> inCollection(String collection);
	}

	/**
	 * Terminating operations invoking the actual aggregation execution.
	 *
	 * @param <T>
	 * @author Christoph Strobl
	 * @since 2.0
	 */
	interface DoAggregate<T> {

		/**
		 * Apply pipeline operations as specified in the given {@literal aggregation}.
		 *
		 * @param aggregation must not be {@literal null}.
		 * @return
		 */
		AggregationResults<T> process(Aggregation aggregation);

		/**
		 * Apply pipeline operations as specified in the given {@literal aggregation}. <br />
		 * Returns a {@link CloseableIterator} that wraps the a Mongo DB {@link com.mongodb.Cursor}
		 *
		 * @param aggregation must not be {@literal null}.
		 * @return
		 */
		CloseableIterator<T> streamProcess(Aggregation aggregation);
	}

	/**
	 * @param <T>
	 * @author Christoph Strobl
	 * @since 2.0
	 */
	interface AggregationOperation<T> extends InCollection<T>, DoAggregate<T> {

	}

}
