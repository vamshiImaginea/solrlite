package com.subsolr.contextprocessor.factory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.CharFilterFactory;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.Version;
import org.apache.solr.analysis.TokenizerChain;
import org.apache.solr.common.SolrException;
import org.apache.solr.core.Config;
import org.apache.solr.util.DOMUtil;
import org.apache.solr.util.plugin.AbstractPluginLoader;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.subsolr.contextprocessor.model.FieldTypeDefinition;

public final class FieldTypeDefinitionFactory {
   private String name = "name";
   private String className = "class";
   private String positionIncrementGap = "positionIncrementGap";
   private String analyzer = "analyzer";
   private String similarity = "similarity";
   
   
   public FieldTypeDefinition getInstance(final Node fieldDefinition) {
      return processChild(fieldDefinition);
   }

   private FieldTypeDefinition processChild(final Node fieldDefinition) {
      FieldTypeDefinition fieldTypeDefinition = new FieldTypeDefinition();

      name = fieldDefinition.getNodeName();

      return null;
   }
   
   private void readFieldContextXml(InputSource is){
      
   }

   private Analyzer extractAnalyzerDefnition(final Node node) {
      // parent node used to be passed in as "fieldtype"
      // if (!fieldtype.hasChildNodes()) return null;
      // Node node = DOMUtil.getChild(fieldtype,"analyzer");

      if (node == null) return null;
      NamedNodeMap attrs = node.getAttributes();
      String analyzerName = DOMUtil.getAttr(attrs,"class");
      if (analyzerName != null) {
        try {
          // No need to be core-aware as Analyzers are not in the core-aware list
          final Class<? extends Analyzer> clazz = loader.findClass
            (analyzerName).asSubclass(Analyzer.class);

          try {
            // first try to use a ctor with version parameter 
            // (needed for many new Analyzers that have no default one anymore)
            Constructor<? extends Analyzer> cnstr = clazz.getConstructor(Version.class);
            final String matchVersionStr = DOMUtil.getAttr(attrs, LUCENE_MATCH_VERSION_PARAM);
            final Version luceneMatchVersion = (matchVersionStr == null) ?
              solrConfig.luceneMatchVersion : Config.parseLuceneVersionString(matchVersionStr);
            if (luceneMatchVersion == null) {
              throw new SolrException
                ( SolrException.ErrorCode.SERVER_ERROR,
                  "Configuration Error: Analyzer '" + clazz.getName() +
                  "' needs a 'luceneMatchVersion' parameter");
            }
            return cnstr.newInstance(luceneMatchVersion);
          } catch (NoSuchMethodException nsme) {
            // otherwise use default ctor
            return clazz.newInstance();
          }
        } catch (Exception e) {
          log.error("Cannot load analyzer: "+analyzerName, e);
          throw new SolrException( SolrException.ErrorCode.SERVER_ERROR,
                                   "Cannot load analyzer: "+analyzerName, e );
        }
      }

      XPath xpath = XPathFactory.newInstance().newXPath();

      // Load the CharFilters
      // --------------------------------------------------------------------------------
      final ArrayList<CharFilterFactory> charFilters = new ArrayList<CharFilterFactory>();
      AbstractPluginLoader<CharFilterFactory> charFilterLoader =
        new AbstractPluginLoader<CharFilterFactory>( "[schema.xml] analyzer/charFilter", false, false )
      {
        @Override
        protected void init(CharFilterFactory plugin, Node node) throws Exception {
          if( plugin != null ) {
            final Map<String,String> params = DOMUtil.toMapExcept(node.getAttributes(),"class");
            // copy the luceneMatchVersion from config, if not set
            if (!params.containsKey(LUCENE_MATCH_VERSION_PARAM))
              params.put(LUCENE_MATCH_VERSION_PARAM, solrConfig.luceneMatchVersion.toString());
            plugin.init( params );
            charFilters.add( plugin );
          }
        }

        @Override
        protected CharFilterFactory register(String name, CharFilterFactory plugin) throws Exception {
          return null; // used for map registration
        }
      };
      charFilterLoader.load( solrConfig.getResourceLoader(), (NodeList)xpath.evaluate("./charFilter", node, XPathConstants.NODESET) );

      // Load the Tokenizer
      // Although an analyzer only allows a single Tokenizer, we load a list to make sure
      // the configuration is ok
      // --------------------------------------------------------------------------------
      final ArrayList<TokenizerFactory> tokenizers = new ArrayList<TokenizerFactory>(1);
      AbstractPluginLoader<TokenizerFactory> tokenizerLoader =
        new AbstractPluginLoader<TokenizerFactory>( "[schema.xml] analyzer/tokenizer", false, false )
      {
        @Override
        protected void init(TokenizerFactory plugin, Node node) throws Exception {
          if( !tokenizers.isEmpty() ) {
            throw new SolrException( SolrException.ErrorCode.SERVER_ERROR,
                "The schema defines multiple tokenizers for: "+node );
          }
          final Map<String,String> params = DOMUtil.toMapExcept(node.getAttributes(),"class");
          // copy the luceneMatchVersion from config, if not set
          if (!params.containsKey(LUCENE_MATCH_VERSION_PARAM))
            params.put(LUCENE_MATCH_VERSION_PARAM, solrConfig.luceneMatchVersion.toString());
          plugin.init( params );
          tokenizers.add( plugin );
        }

        @Override
        protected TokenizerFactory register(String name, TokenizerFactory plugin) throws Exception {
          return null; // used for map registration
        }
      };
      tokenizerLoader.load( loader, (NodeList)xpath.evaluate("./tokenizer", node, XPathConstants.NODESET) );
      
      // Make sure something was loaded
      if( tokenizers.isEmpty() ) {
        throw new SolrException(SolrException.ErrorCode.SERVER_ERROR,"analyzer without class or tokenizer & filter list");
      }
      

      // Load the Filters
      // --------------------------------------------------------------------------------
      final ArrayList<TokenFilterFactory> filters = new ArrayList<TokenFilterFactory>();
      AbstractPluginLoader<TokenFilterFactory> filterLoader = 
        new AbstractPluginLoader<TokenFilterFactory>( "[schema.xml] analyzer/filter", false, false )
      {
        @Override
        protected void init(TokenFilterFactory plugin, Node node) throws Exception {
          if( plugin != null ) {
            final Map<String,String> params = DOMUtil.toMapExcept(node.getAttributes(),"class");
            // copy the luceneMatchVersion from config, if not set
            if (!params.containsKey(LUCENE_MATCH_VERSION_PARAM))
              params.put(LUCENE_MATCH_VERSION_PARAM, solrConfig.luceneMatchVersion.toString());
            plugin.init( params );
            filters.add( plugin );
          }
        }

        @Override
        protected TokenFilterFactory register(String name, TokenFilterFactory plugin) throws Exception {
          return null; // used for map registration
        }
      };
      filterLoader.load( loader, (NodeList)xpath.evaluate("./filter", node, XPathConstants.NODESET) );

      return new TokenizerChain(charFilters.toArray(new CharFilterFactory[charFilters.size()]),
          tokenizers.get(0), filters.toArray(new TokenFilterFactory[filters.size()]));
    
   }

   private Similarity extractSimilarityDefinition(final Node fieldDefinition) {
      return null;
   }
}
