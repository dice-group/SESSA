package org.lucene46.codecs.lucene3x;

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

import org.lucene46.codecs.DocValuesConsumer;
import org.lucene46.codecs.DocValuesProducer;
import org.lucene46.codecs.NormsFormat;
import org.lucene46.index.SegmentReadState;
import org.lucene46.index.SegmentWriteState;

/**
 * Lucene3x ReadOnly NormsFormat implementation
 * @deprecated (4.0) This is only used to read indexes created
 * before 4.0.
 * @lucene.experimental
 */
@Deprecated
class Lucene3xNormsFormat extends NormsFormat {

  @Override
  public DocValuesConsumer normsConsumer(SegmentWriteState state) throws IOException {
    throw new UnsupportedOperationException("this codec can only be used for reading");
  }

  @Override
  public DocValuesProducer normsProducer(SegmentReadState state) throws IOException {
    return new Lucene3xNormsProducer(state.directory, state.segmentInfo, state.fieldInfos, state.context);
  }
}