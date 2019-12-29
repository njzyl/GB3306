package cn.tx.test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.management.Query;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneTest2 {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void addIndex() throws Exception {
		
		IndexWriter iw = getIndexWriter();
		//采集原始文档
		File file = new File("E:\\searchsource\\hibernate.txt");
		String fileName = file.getName();
		String content = FileUtils.readFileToString(file);
		long size = FileUtils.sizeOf(file);
		String path1 = file.getPath();
		
		Field fName = new TextField("fileName", fileName, Store.YES);
		Field fcontent = new TextField("content", content, Store.NO);
		Field fsize = new LongField("size", size, Store.YES);
		Field fpath = new TextField("path", path1, Store.YES);
		
		//创建文档对象
		Document doc = new Document();
		//把域加入到文档中
		doc.add(fName);
		doc.add(fcontent);
		doc.add(fsize);
		doc.add(fpath);
		iw.addDocument(doc);	
	
		//提交
		iw.commit();
		iw.close();
		
	}
	
	@Test
	public void deleteIndex() throws Exception {
		
		IndexWriter iw = getIndexWriter();
		iw.deleteAll();
		iw.commit();
		iw.close();
		
	}
	
	@Test
	public void deleteIndexByQuery() throws Exception {
		
		IndexWriter iw = getIndexWriter();
		
		//创建语汇单元项
		Term term = new Term("fileName", "spring");
		//创建根据语汇单元的查询对象
		TermQuery query = new TermQuery(term);
		iw.deleteDocuments(query);
		iw.commit();
		iw.close();
		
	}
	
	
	public IndexWriter getIndexWriter() throws Exception{
		//获得索引库的
		Path path = Paths.get("E:\\index_loc");
		//打开索引库
		FSDirectory dir = FSDirectory.open(path);
		//创建分词器
		Analyzer al = new IKAnalyzer();
		//创建索引的写入的配置对象
		IndexWriterConfig iwc = new IndexWriterConfig(al);
		//创索引的Writer
		IndexWriter iw = new IndexWriter(dir, iwc);
		return iw;
	}

}
