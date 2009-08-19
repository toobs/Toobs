/*
 * This file is licensed to the Toobs Framework Group under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Toobs Framework Group licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file 
 * except in compliance with the License.  You may obtain a copy of the 
 * License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.toobsframework.servlet.filters.compression;

import java.io.PrintWriter;
import java.io.Writer;
import java.io.OutputStream;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class FilterPrintWriter extends PrintWriter {

  //private static Log log = LogFactory.getLog(FilterPrintWriter.class);

  private boolean closed = false;

  public FilterPrintWriter (Writer out) {
      this(out, false);
  }

  public FilterPrintWriter(Writer out,
                     boolean autoFlush) {
      super(out, autoFlush);
  }

  public FilterPrintWriter(OutputStream out) {
      super(out, false);
  }

  public void close() {
    //super.close();
    //closed = true;
  }

  public void reallyClose() {
    super.close();
    closed = true;
  }

  public boolean closed() {
    return closed;
  }

  public void write(String s, int off, int len) {
    super.write(s, off, len);
  }

  public void write(char buf[], int off, int len) {
    super.write(buf, off, len);
  }

  public void print(String s) {
    super.print(s);
  }

}
