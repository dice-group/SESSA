package org.lucene46.codecs.lucene41;

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

import org.lucene46.codecs.Codec;
import org.lucene46.codecs.FieldInfosFormat;
import org.lucene46.codecs.FilterCodec;
import org.lucene46.codecs.LiveDocsFormat;
import org.lucene46.codecs.PostingsFormat;
import org.lucene46.codecs.SegmentInfoFormat;
import org.lucene46.codecs.DocValuesFormat;
import org.lucene46.codecs.NormsFormat;
import org.lucene46.codecs.StoredFieldsFormat;
import org.lucene46.codecs.StoredFieldsWriter;
import org.lucene46.codecs.TermVectorsFormat;
import org.lucene46.codecs.compressing.CompressingStoredFieldsFormat;
import org.lucene46.codecs.compressing.CompressionMode;
import org.lucene46.codecs.lucene40.Lucene40DocValuesFormat;
import org.lucene46.codecs.lucene40.Lucene40FieldInfosFormat;
import org.lucene46.codecs.lucene40.Lucene40LiveDocsFormat;
import org.lucene46.codecs.lucene40.Lucene40NormsFormat;
import org.lucene46.codecs.lucene40.Lucene40SegmentInfoFormat;
import org.lucene46.codecs.lucene40.Lucene40TermVectorsFormat;
import org.lucene46.codecs.perfield.PerFieldPostingsFormat;
import org.lucene46.index.SegmentInfo;
import org.lucene46.store.Directory;
import org.lucene46.store.IOContext;

/**
 * Implements the Lucene 4.1 index format, with configurable per-field postings formats.
 * <p>
 * If you want to reuse functionality of this codec in another codec, extend
 * {@link FilterCodec}.
 *
 * @see org.apache.lucene.codecs.lucene41 package documentation for file format details.
 * @deprecated Only for reading old 4.0 segments
 * @lucene.experimental
 */
@Deprecated
public class Lucene41Codec extends Codec {
  // TODO: slightly evil
  private final StoredFieldsFormat fieldsFormat = new CompressingStoredFieldsFormat("Lucene41StoredFields", CompressionMode.FAST, 1 << 14) {
    @Override
    public StoredFieldsWriter fieldsWriter(Directory directory, SegmentInfo si, IOContext context) throws IOException {
      throw new UnsupportedOperationException("this codec can only be used for reading");
    }
  };
  private final TermVectorsFormat vectorsFormat = new Lucene40TermVectorsFormat();
  private final FieldInfosFormat fieldInfosFormat = new Lucene40FieldInfosFormat();
  private final SegmentInfoFormat infosFormat = new Lucene40SegmentInfoFormat();
  private final LiveDocsFormat liveDocsFormat = new Lucene40LiveDocsFormat();
  
  private final PostingsFormat postingsFormat = new PerFieldPostingsFormat() {
    @Override
    public PostingsFormat getPostingsFormatForField(String field) {
      return Lucene41Codec.this.getPostingsFormatForField(field);
    }
  };

  /** Sole constructor. */
  public Lucene41Codec() {
    super("Lucene41");
  }
  
  // TODO: slightly evil
  @Override
  public StoredFieldsFormat storedFieldsFormat() {
    return fieldsFormat;
  }
  
  @Override
  public final TermVectorsFormat termVectorsFormat() {
    return vectorsFormat;
  }

  @Override
  public final PostingsFormat postingsFormat() {
    return postingsFormat;
  }
  
  @Override
  public FieldInfosFormat fieldInfosFormat() {
    return fieldInfosFormat;
  }
  
  @Override
  public SegmentInfoFormat segmentInfoFormat() {
    return infosFormat;
  }
  
  @Override
  public final LiveDocsFormat liveDocsFormat() {
    return liveDocsFormat;
  }

  /** Returns the postings format that should be used for writing 
   *  new segments of <code>field</code>.
   *  
   *  The default implementation always returns "Lucene41"
   */
  public PostingsFormat getPostingsFormatForField(String field) {
    return defaultFormat;
  }
  
  @Override
  public DocValuesFormat docValuesFormat() {
    return dvFormat;
  }

  private final PostingsFormat defaultFormat = PostingsFormat.forName("Lucene41");
  private final DocValuesFormat dvFormat = new Lucene40DocValuesFormat();
  private final NormsFormat normsFormat = new Lucene40NormsFormat();

  @Override
  public NormsFormat normsFormat() {
    return normsFormat;
  }
}