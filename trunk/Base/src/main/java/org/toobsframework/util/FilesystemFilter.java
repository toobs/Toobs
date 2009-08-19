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
package org.toobsframework.util;

import java.io.File;
import java.io.FilenameFilter;
import javax.swing.filechooser.FileFilter;

public class FilesystemFilter extends FileFilter implements FilenameFilter {

  private String fileext[];
  private String descr;
  private boolean accDirs;

  public FilesystemFilter(String fileext, String descr) {
    this(fileext, descr, true);
  }

  public FilesystemFilter(String fileext, String descr, boolean accDirs) {
    this(new String[] { fileext }, descr, accDirs);
  }

  public FilesystemFilter(String fileext[], String descr, boolean accDirs) {
    this.fileext = (String[]) (String[]) fileext.clone();
    this.descr = descr;
    this.accDirs = accDirs;
  }

  public boolean accept(File dir, String name) {
    File f = new File(dir, name);
    if (f.isDirectory() && acceptsDirectories())
      return true;
    for (int i = 0; i < fileext.length; i++)
      if (name.endsWith(fileext[i]))
        return true;

    return false;
  }

  public boolean accept(File dir) {
    if (dir.isDirectory() && acceptsDirectories())
      return true;
    for (int i = 0; i < fileext.length; i++)
      if (dir.getName().endsWith(fileext[i]))
        return true;

    return false;
  }

  public String getDescription() {
    return descr;
  }

  public void acceptDirectories(boolean b) {
    accDirs = b;
  }

  public boolean acceptsDirectories() {
    return accDirs;
  }

}
