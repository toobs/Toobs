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
