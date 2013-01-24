package com.wenbo.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Demo {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception{
		Demo demo1 = new Demo();
		demo1.createIndex();
		demo1.search();
	}
	
	public void createIndex()throws IOException{
		File file = new File("/index");
		Directory directory = FSDirectory.open(file);
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
		config.setOpenMode(OpenMode.CREATE);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Demo.class.getResourceAsStream("/source.txt")));
        String str = null;
        int i = 0;
        Document document = null;
        while((str=bufferedReader.readLine()) != null){
        	document = new Document();
        	document.add(new IntField("num",i,Store.YES));
        	document.add(new TextField("content",str,Store.NO));
        	indexWriter.addDocument(document);
        	i++;
        }
        indexWriter.commit();
        indexWriter.close();
	}
	
	public void search()throws IOException, ParseException{
		File file = new File("/index");
		Directory directory = FSDirectory.open(file);
		IndexReader indexReader = DirectoryReader.open(directory);
		indexReader = new MultiReader(indexReader);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
		QueryParser parser = new QueryParser(Version.LUCENE_40, "content", analyzer);
		Query query = parser.parse("Google");
		TopDocs topDocs = indexSearcher.search(query, null, 1000);
		System.out.println("查询结果总数---" + topDocs.totalHits+"最大的评分--"+topDocs.getMaxScore()); 
	    ScoreDoc[] hits = topDocs.scoreDocs;
	    ScoreDoc hit = null;
	    for(int i = 0; i < hits.length; i++){
	    	hit = hits[i];
	    	System.out.println("id--" + hit.doc + "---scors--" + hit.score+"---index--"+hit.shardIndex);
	    	Document coDocument = indexSearcher.doc(hit.doc);
	    	System.out.println(coDocument.get("num"));
	    	System.out.println(coDocument.get("content"));
	    }
	}

}
