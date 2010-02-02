package autobase;

import liquibase.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.util.zip.*;
import org.apache.log4j.*;
import org.codehaus.groovy.grails.commons.ApplicationHolder;
import org.codehaus.groovy.grails.web.context.ServletContextHolder;

public class WarFileOpener implements FileOpener {

  private static final Logger logger = Logger.getLogger(WarFileOpener.class);
  String baseDirectory;

  public WarFileOpener() throws IOException {
    this(expandStream(createZipStream()).getAbsolutePath());
  }

  /**
   * Creates using  a supplied base directory.
   *
   * @param base The path to use to resolve relative paths
   */
  public WarFileOpener(String base) {
    if (new File(base).isFile())
      throw new IllegalArgumentException("base must be a directory");
    baseDirectory = base;
  }

  /**
  * Expands the given stream and returns the base directory where files were expanded into.
  */
  private static File expandStream(final ZipInputStream source) throws IOException {
    final String timestamp = Long.toString(System.currentTimeMillis(), 16);
    final File base = new File(System.getProperty("java.io.tmpdir"), "autobase-" + timestamp).getAbsoluteFile();
    base.mkdirs();
    logger.debug("Base directory: " + base.getAbsolutePath());
    deleteDirOnExit(base);
    for(ZipEntry entry = source.getNextEntry(); entry != null; entry = source.getNextEntry()) {
      final File entryFile = new File(base, entry.getName()).getAbsoluteFile();
      logger.debug("Writing out file: " + entryFile.getAbsolutePath());
      entryFile.getParentFile().mkdirs();
      if(entry.isDirectory()) {
        source.closeEntry();
        entryFile.mkdir();
      } else {
        entryFile.createNewFile();
        final OutputStream entryStream = new BufferedOutputStream(new FileOutputStream(entryFile));
        for(int c = source.read(); c != -1; c = source.read()) {
          entryStream.write(c);
        }
        source.closeEntry();
        entryStream.close();
      }
    }
    source.close();
    return base;
  }

  private static void deleteDirOnExit(final File base) {
    final Runnable deleteBase = new Runnable() {
      public void run() {
        delete(base);
      }

      private void delete(final File dir) {
        final File[] files = dir.listFiles();
        if(files != null) {
          for(final File file : files) {
            delete(file);
          }
        }
        dir.delete();
      }
    };
    Runtime.getRuntime().addShutdownHook(new Thread(deleteBase));
  }

  private static ZipInputStream createZipStream() throws IOException {
    InputStream is = null;

    try {
      is = ApplicationHolder.getApplication().getParentContext().getResource("/autobase.zip").getInputStream();
      is.available();
    } catch (Exception e) {
      try {
        is = ApplicationHolder.getApplication().getParentContext().getResource("/WEB-INF/classes/autobase.zip").getInputStream();
        is.available();
      } catch (Exception e1) {
        try {
          is = new ClassLoaderFileOpener().getResourceAsStream("/autobase.zip");
          is.available();
        } catch (Exception e2) {
          try {
            is = ServletContextHolder.getServletContext().getResourceAsStream("/WEB-INF/classes/autobase.zip");
            is.available();
          } catch (Exception e3) { /* swallon it - nothing we can do */ }
        }
      }
    }

    return new ZipInputStream(new BufferedInputStream(is));
  }

  /**
   * Opens a stream on a file, resolving to the baseDirectory if the
   * file is relative.
   */
  public InputStream getResourceAsStream(String file) throws IOException {
    File absoluteFile = new File(file);
    File relativeFile = (baseDirectory == null) ? new File(file) : new File(baseDirectory, file);

    if (absoluteFile.exists() && absoluteFile.isFile() && absoluteFile.isAbsolute()) {
      return new FileInputStream(absoluteFile);
    } else if (relativeFile.exists() && relativeFile.isFile()) {
      return new FileInputStream(relativeFile);
    } else {
      return null;

    }
  }

  public Enumeration<URL> getResources(String packageName) throws IOException {
    String directoryPath;
    boolean isPackageAbsolute = new File(packageName).isAbsolute();

    if(isPackageAbsolute || baseDirectory == null) {
      directoryPath = packageName;
    } else {
      directoryPath = baseDirectory + File.separator + packageName;
    }

    File directoryFile = new File(directoryPath);
    if (!directoryFile.exists()) {
      return new Vector<URL>().elements();
    }
    File[] files = directoryFile.listFiles();

    List<URL> results = new ArrayList<URL>();

    for (File f : files) {
      results.add(f.toURI().toURL());
    }

    final Iterator<URL> it = results.iterator();
    return new Enumeration<URL>() {
      public boolean hasMoreElements() {
        return it.hasNext();
      }

      public URL nextElement() {
        return it.next();
      }
    };
  }

  public ClassLoader toClassLoader() {
    try {
      return new URLClassLoader(new URL[]{new URL("file://" + baseDirectory)});
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
 }
}
