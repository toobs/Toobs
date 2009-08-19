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

import java.io.*;
import javax.servlet.ServletOutputStream;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class FilterOutputStream extends ServletOutputStream
{

  //private static Log log = LogFactory.getLog(FilterOutputStream.class);

  private DataOutputStream stream;

  public FilterOutputStream(OutputStream outputstream) {
    stream = new DataOutputStream(outputstream);
  }

  public void write(int i) throws IOException {
    stream.write(i);
  }

  public void write(byte buf[]) throws IOException {
    stream.write(buf);
  }

  public void write(byte buf[], int i, int j) throws IOException {
    stream.write(buf, i, j);
  }

}
