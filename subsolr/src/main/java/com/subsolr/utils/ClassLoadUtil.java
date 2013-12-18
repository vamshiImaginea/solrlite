package com.subsolr.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.solr.common.SolrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassLoadUtil {
   public static final Logger log = LoggerFactory.getLogger(ClassLoadUtil.class);
   private static final Class<?>[] parameters = new Class[] { URL.class };
   private static Map<String, String> classNameCache = new ConcurrentHashMap<String, String>();
   private URLClassLoader classLoader;
   private final String libDir;

   public ClassLoadUtil(URLClassLoader parent, String libDir) {
      super();
      this.classLoader = parent;
      this.libDir = libDir;

      this.classLoader = createClassLoader(null, parent);
      addToClassLoader(libDir, null);
   }

   /**
    * Adds every file/dir found in the baseDir which passes the specified Filter to the ClassLoader used by this
    * ResourceLoader. This method <b>MUST</b> only be called prior to using this ResourceLoader to get any resources,
    * otherwise it's behavior will be non-deterministic.
    * 
    * @param baseDir
    *           base directory whose children (either jars or directories of classes) will be in the classpath, will be
    *           resolved relative the instance dir.
    * @param filter
    *           The filter files must satisfy, if null all files will be accepted.
    */
   void addToClassLoader(final String baseDir, final FileFilter filter) {
      File base = new File(baseDir);
      this.classLoader = replaceClassLoader(classLoader, base, filter);
   }

   /**
    * Adds the specific file/dir specified to the ClassLoader used by this ResourceLoader. This method <b>MUST</b> only
    * be called prior to using this ResourceLoader to get any resources, otherwise it's behavior will be
    * non-deterministic.
    * 
    * @param path
    *           A jar file (or directory of classes) to be added to the classpath, will be resolved relative the
    *           instance dir.
    */
   void addToClassLoader(final String libDir) {
      final File file = new File(libDir);
      if (file.canRead()) {
         this.classLoader = replaceClassLoader(classLoader, file.getParentFile(), new FileFilter() {
            public boolean accept(File pathname) {
               return pathname.equals(file);
            }
         });
      } else {
         log.error("Can't find (or read) file to add to classloader: " + file);
      }
   }

   static URLClassLoader createClassLoader(final File libDir, ClassLoader parent) {
      if (null == parent) {
         parent = Thread.currentThread().getContextClassLoader();
      }
      return replaceClassLoader(URLClassLoader.newInstance(new URL[0], parent), libDir, null);
   }

   private static URLClassLoader replaceClassLoader(final URLClassLoader oldLoader, final File base,
         final FileFilter filter) {
      if (null != base && base.canRead() && base.isDirectory()) {
         File[] files = base.listFiles(filter);

         if (null == files || 0 == files.length)
            return oldLoader;

         URL[] oldElements = oldLoader.getURLs();
         URL[] elements = new URL[oldElements.length + files.length];
         System.arraycopy(oldElements, 0, elements, 0, oldElements.length);

         for (int j = 0; j < files.length; j++) {
            try {
               URL element = files[j].toURI().normalize().toURL();
               log.info("Adding '" + element.toString() + "' to classloader");
               elements[oldElements.length + j] = element;
            } catch (MalformedURLException e) {
               SolrException.log(log, "Can't add element to classloader: " + files[j], e);
            }
         }
         return URLClassLoader.newInstance(elements, oldLoader.getParent());
      }
      // are we still here?
      return oldLoader;
   }

   public boolean isClassLoaded(String className) {
      try {
         Class cls = Class.forName(className);
         return true;
      } catch (Exception ex) {
         ex.printStackTrace();
         return false;
      }
   }

   public static void addFile(String s) throws IOException {
      File f = new File(s);
      addFile(f);
   }

   public static void addFile(File f) throws IOException {
      addURL(f.toURI().toURL());
   }

   /**
    * Adds the jar in the current class loader.
    * 
    * @param u
    * @throws IOException
    */
   public static void addURL(URL u) throws IOException {
      URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
      Class<?> sysclass = URLClassLoader.class;
      try {
         Method method = sysclass.getDeclaredMethod("addURL", parameters);
         method.setAccessible(true);
         method.invoke(sysloader, new Object[] { u });
      } catch (Throwable t) {
         t.printStackTrace();
         throw new IOException("Error, could not add URL to system classloader");
      }// end try catch
   }

   public Class findClass(String cname, String... subpackages) {
      if (subpackages == null || subpackages.length == 0) {

         String c = classNameCache.get(cname);
         if (c != null) {
            try {
               return Class.forName(c, true, classLoader);
            } catch (ClassNotFoundException e) {
               // this is unlikely
               log.error("Unable to load cached class-name :  " + c + " for shortname : " + cname + e);
            }

         }
      }
      Class clazz = null;
      // first try cname == full name
      try {
         return Class.forName(cname, true, classLoader);
      } catch (ClassNotFoundException e) {

         throw new RuntimeException("Error loading class '" + cname);
      } finally {
         // cache the shortname vs FQN if it is loaded by the webapp classloader and it is loaded
         // using a shortname
         if (clazz != null && clazz.getClassLoader() == ClassLoadUtil.class.getClassLoader()
               && !cname.equals(clazz.getName()) && (subpackages.length == 0)) {
            // store in the cache
            classNameCache.put(cname, clazz.getName());
         }
      }
   }

   public ClassLoader getClassLoader() {
      return classLoader;
   }

}
