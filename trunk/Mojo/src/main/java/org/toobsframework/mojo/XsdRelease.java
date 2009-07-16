package org.toobsframework.mojo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Goal processes xsds to be readied for release.
 *
 * @goal release-xsd
 * 
 * @phase package
 */
public class XsdRelease extends AbstractMojo {
  /**
   * Location of the original files.
   * @parameter expression="${release-xsd.source}"
   * @required
   */
  private File source;

  /**
   * Location of the original files.
   * @parameter expression="${release-xsd.target}"
   * @required
   */
  private File target;
  
  /**
   * version stamp of the files.
   * @parameter expression="${release-xsd.version}"
   * @required
   */
  private String version;
  
  /**
   * is the output verbose?
   * @parameter expression="${release-xsd.verbose}" default-value="false"
   */
  private boolean verbose;
  
  /**
   * optional url for the replacement
   * @parameter expression="${release-xsd.url}" default=""
   */
  private String url;

  @SuppressWarnings("unchecked")
  public void execute() throws MojoExecutionException {
    int count = 0;
    int of = 0;
    
    try {
      if (verbose) {
        getLog().info("release-xsd will attempt copy versions of xsds from directory "+ source.getPath() + " to directory " + target.getPath() + " with version " + version);
      }
      
      if (!source.exists()) {
        throw new MojoExecutionException("Cannot copy xsds from " + source.getPath() + " since the directory does not exists");
      }
      if (!source.isDirectory()) {
        throw new MojoExecutionException("Cannot copy xsds from " + source.getPath() + " since it is not a directory");
      }
    
      if (verbose) {
        getLog().info(source.getPath() + " directory was located");
      }

      if (!target.exists()) {
        if(!target.mkdirs()) {
          throw new MojoExecutionException("Cannot create the targetdirectory " + target.getPath());
        }
        if (verbose) {
          getLog().info(target.getPath() + " directory was created");
        }
      } else if (!target.isDirectory()) {
        throw new MojoExecutionException("Cannot copy xsds from " + target.getPath() + " since it is not a directory");
      } else {
        if (verbose) {
          getLog().info(target.getPath() + " directory was located");
        }
      }
      
      File[] sourceFiles = source.listFiles((FileFilter) new SuffixFileFilter(".xsd"));
      for (File inputFile : sourceFiles) {
        of++;
        
        String fileName = replace(inputFile.getName(), ".xsd", "-" + version + ".xsd");
        File outputFile = new File(target, fileName);
        
        if (outputFile.exists() && FileUtils.isFileNewer(outputFile, inputFile)) {
          if (verbose) {
            getLog().info("File " + inputFile.getPath() + " is newer than " + outputFile.getPath() + ".  Skipped.");
          }
          continue;
        }
        
        if (verbose) {
          getLog().info("copying file " + inputFile.getPath() + " to " + outputFile.getPath());
        }
        
        InputStream inputStream = new FileInputStream(inputFile);
        List<String> inputLines;
        try {
          inputLines = IOUtils.readLines(inputStream);
        } finally {
          inputStream.close();
        }
        
        List<String> outputLines = new ArrayList<String>(inputLines.size());
        for (String inputLine : inputLines) {
          String line = replace(inputLine, ".xsd", "-" + version + ".xsd");
          if (url != null && url.length() > 0) {
            line = replace(line, "schemaLocation=\"", "schemaLocation=\"" + url);
          }
          outputLines.add(line);
          /*getLog().info(">>" + line);*/
        }

        OutputStream outputStream = new FileOutputStream(outputFile, false);
        try {
          IOUtils.writeLines(outputLines, "\n", outputStream);
        } finally {
          outputStream.close();
        }
        count++;
      }
      
      if (count == 0) {
        getLog().info("No xsds released - " + of + " are up to date");
      } else {
        getLog().info(count + " xsd(s) out of " + of + " were sucessfully updated");
      }
    } catch (IOException e) {
      throw new MojoExecutionException("Error occurred when trying to release xsds: " + e.getMessage(), e);
    }
  }

  /**
   * <p>Replace all occurrences of a String within another String.</p>
   *
   * <p>A <code>null</code> reference passed to this method is a no-op.</p>
   *
   * @see #replace(String text, String repl, String with, int max)
   * @param text text to search and replace in
   * @param repl String to search for
   * @param with String to replace with
   * @return the text with any replacements processed
   */
  public static String replace( String text, String repl, String with )
  {
      return replace( text, repl, with, -1 );
  }

  /**
   * <p>Replace a String with another String inside a larger String,
   * for the first <code>max</code> values of the search String.</p>
   *
   * <p>A <code>null</code> reference passed to this method is a no-op.</p>
   *
   * @param text text to search and replace in
   * @param repl String to search for
   * @param with String to replace with
   * @param max maximum number of values to replace, or <code>-1</code> if no maximum
   * @return the text with any replacements processed
   */
  public static String replace( String text, String repl, String with, int max )
  {
      if ( text == null || repl == null || with == null || repl.length() == 0 )
      {
          return text;
      }

      StringBuffer buf = new StringBuffer( text.length() );
      int start = 0, end = 0;
      while ( ( end = text.indexOf( repl, start ) ) != -1 )
      {
          buf.append( text.substring( start, end ) ).append( with );
          start = end + repl.length();

          if ( --max == 0 )
          {
              break;
          }
      }
      buf.append( text.substring( start ) );
      return buf.toString();
  }
}
