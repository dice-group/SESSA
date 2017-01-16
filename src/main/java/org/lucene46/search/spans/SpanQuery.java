package org.lucene46.search.spans;


/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.Map;

import org.lucene46.index.AtomicReaderContext;
import org.lucene46.index.Term;
import org.lucene46.index.TermContext;
import org.lucene46.search.Query;
import org.lucene46.search.IndexSearcher;
import org.lucene46.search.Weight;
import org.lucene46.util.Bits;

/** Base class for span-based queries. */
public abstract class SpanQuery extends Query {
  /** Expert: Returns the matches for this query in an index.  Used internally
   * to search for spans. */
  public abstract Spans getSpans(AtomicReaderContext context, Bits acceptDocs, Map<Term,TermContext> termContexts) throws IOException;

  /** Returns the name of the field matched by this query.*/
  public abstract String getField();

  @Override
  public Weight createWeight(IndexSearcher searcher) throws IOException {
    return new SpanWeight(this, searcher);
  }

}