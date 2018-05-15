package com.itheima.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class IndexManager {
	
	@Test
	public void testAddIndex() throws IOException {
		Directory dir=FSDirectory.open(new File("D:\\IndexRespo"));
//		Analyzer analyzer = new StandardAnalyzer();
		Analyzer analyzer = new IKAnalyzer();
		IndexWriterConfig config=new IndexWriterConfig(Version.LATEST,analyzer);
		IndexWriter indexWriter = new IndexWriter(dir,config);
		File files= new File("E:\\上课用的查询资料searchsource");
		File[] listFiles = files.listFiles();
		for (File file : listFiles) {
			Document doc = new Document();
			Field fileNameField = new TextField("name",file.getName(),Store.YES);
			doc.add(fileNameField);
			
			Field filePathField = new StoredField("path",file.getPath());
			doc.add(filePathField);
			
			long sizeOf=FileUtils.sizeOf(file);
			Field fileSizeField = new LongField("size",sizeOf,Store.YES);
			doc.add(fileSizeField);
			
			String fileContext = FileUtils.readFileToString(file);
			Field fileContextField = new TextField("content",fileContext,Store.YES);
			doc.add(fileContextField);
			
			indexWriter.addDocument(doc);
		}
		indexWriter.close();
	}
	
	@Test
	public void testSearchIndex() throws IOException {
		Directory directory=FSDirectory.open(new File("D:\\IndexRespo"));
		IndexReader indexReader=DirectoryReader.open(directory);
		IndexSearcher indexSearcher=new IndexSearcher(indexReader);
		Query query=new TermQuery(new Term("context","spring"));
		TopDocs topDocs=indexSearcher.search(query, 100);
		System.out.println("总条数："+topDocs.totalHits);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docId=scoreDoc.doc;
			Document doc=indexSearcher.doc(docId);
			System.out.println(doc.get("name"));
			System.out.println(doc.get("size"));
			System.out.println(doc.get("path"));
			System.out.println(doc.get("content"));
			System.out.println("----------------------------");
			
		}
		indexReader.close();
	}
	
	@Test
	public void testCh() throws IOException {
		Analyzer analyzer = new IKAnalyzer();
		String str="nihaoa你少是娃娃多少";
		TokenStream tokenStream = analyzer.tokenStream("test", str);
		tokenStream.reset();
		CharTermAttribute addAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		while(tokenStream.incrementToken()) {
			System.out.println(addAttribute);
		}
	}
	
	@Test
	public void addDocument() throws Exception {
		Directory directory=FSDirectory.open(new File("D:\\IndexRespo"));
		IndexWriterConfig config=new IndexWriterConfig(Version.LATEST,new IKAnalyzer());
		IndexWriter indexWriter = new IndexWriter(directory,config);
		Document doc = new Document();
		TextField textField = new TextField("name","1ddd新添加的文档spring",Store.YES);
		textField.setBoost(10);
		doc.add(textField);
		doc.add(new TextField("content","文档的内容1",Store.YES));
		doc.add(new TextField("content","文档的内容2",Store.YES));
		indexWriter.addDocument(doc);
		indexWriter.close();
	}
	
	public IndexWriter getIndexWriter() throws Exception {
		Directory directory=FSDirectory.open(new File("D:\\IndexRespo"));
		IndexWriterConfig config=new IndexWriterConfig(Version.LATEST,new IKAnalyzer());
		IndexWriter indexWriter = new IndexWriter(directory,config);
		return indexWriter;
	}
	
	//删除
	@Test
	public void delete() throws Exception {
		//删除全部
		IndexWriter indexWriter = getIndexWriter();
		indexWriter.deleteAll();
		
		//按查询条件删除
		/*Query query = new TermQuery(new Term("name","apache"));
		indexWriter.deleteDocuments(query);
		indexWriter.commit();*/
		
		indexWriter.close();
		
	}
	
	//修改索引库
	@Test
	public void update() throws Exception {
		IndexWriter indexWriter = getIndexWriter();
		Document doc=new Document();
		TextField nameField = new TextField("name", "自己再IndexWriterConfig一次亲自添加的一个文档spring",Store.YES);
		doc.add(new StoredField("path", "d://sdsds"));
		doc.add(new LongField("size", 100l,Store.YES));
		doc.add(new StringField("content", "自己添加的一个文档自己添加的一个文档自己添加的一个文档",Store.NO));
		
		indexWriter.updateDocument(new Term("name","apache"),doc);
		indexWriter.close();
	}
	
	//查询索引库
	@Test
	public void Query() throws Exception {
		Directory directory=FSDirectory.open(new File("D:\\IndexRespo"));
		IndexReader indexReader=DirectoryReader.open(directory);
		IndexSearcher indexSearcher=new IndexSearcher(indexReader);
		Query query=new TermQuery(new Term("name","spring"));
		
		//文件区间查询
//		Query query=NumericRangeQuery.newLongRange("size", 1l, 1000l, true, true);
		
		//联合查询
//		BooleanQuery query = new BooleanQuery();
//		Query query1=new TermQuery(new Term("context","spring"));
//		Query query2=new TermQuery(new Term("name","spring"));
//		query.add(query1,Occur.SHOULD);
//		query.add(query2,Occur.MUST_NOT);
		
		//分词查询
//		QueryParser queryParser=new QueryParser("name",new IKAnalyzer());
		
//		QueryParser queryParser=new MultiFieldQueryParser(new String[] {"name","content"},new IKAnalyzer());
		
//		Query query = queryParser.parse("spring is a project");
//		
		//查询全部
//		Query query = new MatchAllDocsQuery();
		
		System.out.println("查询语法："+query);
		TopDocs topDocs = indexSearcher.search(query, 100);
		System.out.println("总条数:"+topDocs.totalHits);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docId = scoreDoc.doc;
			Document doc = indexSearcher.doc(docId);
			System.out.println(doc.get("name"));
//			System.out.println(doc.get("size"));
//			System.out.println(doc.get("path"));
//			System.out.println(doc.get("content"));
			System.out.println("----------------------------------------------");
			
		}
		indexReader.close();
	}
	
	public void tes() {
		System.out.println("12345");
	}
}
