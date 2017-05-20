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
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.util.CloseableIterator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Christoph Strobl
 * @since 2.0
 */
public class AggregationOperationSupport implements AggregateOperationBuilder {

	private final MongoTemplate template;

	public AggregationOperationSupport(MongoTemplate template) {

		Assert.notNull(template, "Template must not be null!");
		this.template = template;
	}

	@Override
	public <T> AggregationOperation<T> aggregateAndReturn(Class<T> domainType) {

		Assert.notNull(domainType, "DomainType must not be null!");
		return new AggreationBuilder<T>(template, domainType, null);
	}

	static class AggreationBuilder<T> implements DoAggregate<T>, AggregationOperation<T> {

		private final MongoTemplate template;
		private final Class<T> domainType;
		private final String collection;

		public AggreationBuilder(MongoTemplate template, Class<T> domainType, String collection) {

			this.template = template;
			this.domainType = domainType;
			this.collection = collection;
		}

		@Override
		public DoAggregate<T> inCollection(String collection) {

			Assert.hasText(collection, "Collection must not be null nor empty!");
			return new AggreationBuilder<T>(template, domainType, collection);
		}

		@Override
		public AggregationResults<T> process(Aggregation aggregation) {

			Assert.notNull(aggregation, "Aggregation must not be null!");
			return template.aggregate(aggregation, getCollectionName(aggregation), domainType);
		}

		@Override
		public CloseableIterator<T> streamProcess(Aggregation aggregation) {

			Assert.notNull(aggregation, "Aggregation must not be null!");
			return template.aggregateStream(aggregation, getCollectionName(aggregation), domainType);
		}

		private String getCollectionName(Aggregation aggregation) {

			if (StringUtils.hasText(collection)) {
				return collection;
			}

			if (aggregation instanceof TypedAggregation) {

				if (((TypedAggregation<?>) aggregation).getInputType() != null) {
					return template.determineCollectionName(((TypedAggregation<?>) aggregation).getInputType());
				}
			}

			return template.determineCollectionName(domainType);
		}
	}
}
