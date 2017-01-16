package org.lucene46.codecs.compressing;

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

import org.lucene46.codecs.CodecUtil;
import org.lucene46.codecs.StoredFieldsFormat;
import org.lucene46.codecs.StoredFieldsReader;
import org.lucene46.codecs.StoredFieldsWriter;
import org.lucene46.codecs.lucene40.Lucene40StoredFieldsFormat;
import org.lucene46.index.FieldInfos;
import org.lucene46.index.MergePolicy;
import org.lucene46.index.SegmentInfo;
import org.lucene46.store.Directory;
import org.lucene46.store.IOContext;


/**
 * A {@link StoredFieldsFormat} that is very similar to
 * {@link Lucene40StoredFieldsFormat} but compresses documents in chunks in
 * order to improve the compression ratio.
 * <p>
 * For a chunk size of <tt>chunkSize</tt> bytes, this {@link StoredFieldsFormat}
 * does not support documents larger than (<tt>2<sup>31</sup> - chunkSize</tt>)
 * bytes. In case this is a problem, you should use another format, such as
 * {@link Lucene40StoredFieldsFormat}.
 * <p>
 * For optimal performance, you should use a {@link MergePolicy} that returns
 * segments that have the biggest byte size first.
 * @lucene.experimental
 */
public class CompressingStoredFieldsFormat extends StoredFieldsFormat {

  private final String formatName;
  private final String segmentSuffix;
  private final CompressionMode compressionMode;
  private final int chunkSize;

  /**
   * Create a new {@link CompressingStoredFieldsFormat} with an empty segment 
   * suffix.
   * 
   * @see CompressingStoredFieldsFormat#CompressingStoredFieldsFormat(String, String, CompressionMode, int)
   */
  public CompressingStoredFieldsFormat(String formatName, CompressionMode compressionMode, int chunkSize) {
    this(formatName, "", compressionMode, chunkSize);
  }
  
  /**
   * Create a new {@link CompressingStoredFieldsFormat}.
   * <p>
   * <code>formatName</code> is the name of the format. This name will be used
   * in the file formats to perform
   * {@link CodecUtil#checkHeader(org.apache.lucene.store.DataInput, String, int, int) codec header checks}.
   * <p>
   * <code>segmentSuffix</code> is the segment suffix. This suffix is added to 
   * the result file name only if it's not the empty string.
   * <p>
   * The <code>compressionMode</code> parameter allows you to choose between
   * compression algorithms that have various compression and decompression
   * speeds so that you can pick the one that best fits your indexing and
   * searching throughput. You should never instantiate two
   * {@link CompressingStoredFieldsFormat}s that have the same name but
   * different {@link CompressionMode}s.
   * <p>
   * <code>chunkSize</code> is the minimum byte size of a chunk of documents.
   * A value of <code>1</code> can make sense if there is redundancy across
   * fields. In that case, both performance and compression ratio should be
   * better than with {@link Lucene40StoredFieldsFormat} with compressed
   * fields.
   * <p>
   * Higher values of <code>chunkSize</code> should improve the compression
   * ratio but will require more memory at indexing time and might make document
   * loading a little slower (depending on the size of your OS cache compared
   * to the size of your index).
   *
   * @param formatName the name of the {@link StoredFieldsFormat}
   * @param compressionMode the {@link CompressionMode} to use
   * @param chunkSize the minimum number of bytes of a single chunk of stored documents
   * @see CompressionMode
   */
  public CompressingStoredFieldsFormat(String formatName, String segmentSuffix, 
                                       CompressionMode compressionMode, int chunkSize) {
    this.formatName = formatName;
    this.segmentSuffix = segmentSuffix;
    this.compressionMode = compressionMode;
    if (chunkSize < 1) {
      throw new IllegalArgumentException("chunkSize must be >= 1");
    }
    this.chunkSize = chunkSize;
    
  }

  @Override
  public StoredFieldsReader fieldsReader(Directory directory, SegmentInfo si,
      FieldInfos fn, IOContext context) throws IOException {
    return new CompressingStoredFieldsReader(directory, si, segmentSuffix, fn, 
        context, formatName, compressionMode);
  }

  @Override
  public StoredFieldsWriter fieldsWriter(Directory directory, SegmentInfo si,
      IOContext context) throws IOException {
    return new CompressingStoredFieldsWriter(directory, si, segmentSuffix, context,
        formatName, compressionMode, chunkSize);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(compressionMode=" + compressionMode
        + ", chunkSize=" + chunkSize + ")";
  }

}