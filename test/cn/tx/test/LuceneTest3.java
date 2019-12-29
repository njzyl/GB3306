package cn.tx.test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneTest3 {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void queryIndex() throws IOException{
		Path path = Paths.get("E:\\index_loc");
		FSDirectory open = FSDirectory.open(path);
		//创建索引库 的读取对象
		DirectoryReader reader = DirectoryReader.open(open);
		//创建索引库的搜索对象
		IndexSearcher is = new IndexSearcher(reader);
		//创建语汇单元的对象
		Term term = new Term("content", "apache");
		//创建分词的语汇查询对象
		TermQuery tq = new TermQuery(term);
		//查询
		TopDocs result = is.search(tq, 10);
		//总记录数
		int totalHits = result.totalHits;
		System.out.println("总记录数是："+totalHits);
		
		for(ScoreDoc sd :result.scoreDocs){
			//获得文档的id
			int id = sd.doc;
			//获得文档对象
			Document doc = is.doc(id);
			String fileName = doc.get("fileName");
			String size = doc.get("size");
			String content = doc.get("content");
			String path1 = doc.get("path");
			System.out.println("文件名："+fileName);
			System.out.println("大小："+size);
			System.out.println("内容："+content);
			System.out.println("路径："+path1);
			System.out.println("------------------------");
		}
		
		
	}
	
	/**
	 * 数值的范围查询
	 * @throws Exception
	 */
	@Test
	public void queryIndex1() throws Exception{
		IndexSearcher is = getDirReader();
		//创建数值范围查询对象
		Query tq = NumericRangeQuery.newLongRange("size", 0l, 500l, true, true);
		System.out.println("打印查询条件："+tq);
		printDoc(is, tq);
		
	}
	
	/**
	 * 多个条件的组合查询
	 * @throws Exception
	 * where name = '' and content = !''
	 */
	@Test
	public void queryIndex2() throws Exception{
		IndexSearcher is = getDirReader();
		//创建BooleanQuery查询对象, 这种查询对象可以控制是&还是|或者是！
		BooleanQuery bq = new BooleanQuery();
		//创建一个分词的语汇查询对象
		Query query1 = new TermQuery(new Term("fileName", "spring"));
		Query query2 = new TermQuery(new Term("content", "spring"));
		Query tq = NumericRangeQuery.newLongRange("size", 0l, 500l, true, true);
		//通过BooleanQuery来控制
		bq.add(query1, Occur.MUST);
		bq.add(query2, Occur.SHOULD);
		bq.add(tq, Occur.MUST);
		System.out.println(bq);
		
		
		System.out.println("打印查询条件："+bq);
		printDoc(is, bq);
		
	}
	
	
	/**
	 * 查询条件的解析查询
	 * @throws Exception
	 */
	@Test
	public void queryIndex3() throws Exception{
		IndexSearcher is = getDirReader();
		//创建分词器
		Analyzer al = new IKAnalyzer();
		//创建查询解析对象
		QueryParser qp = new QueryParser("fileName", al);
		//通过qp来解析查询对象
		Query query = qp.parse("今天我们学习全文检索技术Lucene");
		
		
		System.out.println("打印查询条件："+query);
		printDoc(is, query);
		
	}
	
	
	@Test
	public void queryIndex5() throws Exception{
		IndexSearcher is = getDirReader();
		//创建分词器
		Analyzer al = new IKAnalyzer();
		
		//定义多个域
		String [] fields = {"fileName", "content"};
		//创建查询解析对象
		MultiFieldQueryParser mp = new MultiFieldQueryParser(fields, al);
		Query query = mp.parse("今天我们学习全文检索技术Lucene");
		Query tq = NumericRangeQuery.newLongRange("size", 0l, 1024l, true, true);
		
		
		BooleanQuery bq = new BooleanQuery();
		bq.add(query, Occur.MUST);
		bq.add(tq, Occur.MUST);
		
		System.out.println("打印查询条件："+bq);
		printDoc(is, bq);
		
	}
	
	@Test
	public void queryIndex6() throws Exception{
		IndexSearcher is = getDirReader();
		//创建分词器
		Analyzer al = new IKAnalyzer();
		
		//定义多个域
		String [] fields = {"fileName", "content"};
		//创建查询解析对象
		MultiFieldQueryParser mp = new MultiFieldQueryParser(fields, al);
		Query query = mp.parse("今天我们学习全文检索技术Lucene");
		
		
		System.out.println("打印查询条件："+query);
		printDoc(is, query);
		
	}
	
	@Test
	public void queryIndex4() throws Exception{
		IndexSearcher is = getDirReader();
		//创建分词器
		Analyzer al = new IKAnalyzer();
		//创建查询解析对象
		QueryParser qp = new QueryParser("fileName", al);
		//通过qp来解析查询对象
		Query query = qp.parse("fileName:solr OR content:java");
		
		
		System.out.println("打印查询条件："+query);
		printDoc(is, query);
		
	}
	
	public static IndexSearcher getDirReader() throws Exception{
		Path path = Paths.get("E:\\index_loc");
		FSDirectory open = FSDirectory.open(path);
		//创建索引库 的读取对象
		DirectoryReader reader = DirectoryReader.open(open);
		//创建索引库的搜索对象
		IndexSearcher is = new IndexSearcher(reader);
		return is;
	}
	
	/**
	 * 打印文档
	 * @param is
	 * @param tq
	 * @throws Exception
	 */
	public static void printDoc(IndexSearcher is, Query tq) throws Exception{
		//查询
		TopDocs result = is.search(tq, 10);
		//总记录数
		int totalHits = result.totalHits;
		System.out.println("总记录数是："+totalHits);
		
		for(ScoreDoc sd :result.scoreDocs){
			//获得文档的id
			int id = sd.doc;
			//获得文档对象
			Document doc = is.doc(id);
			String fileName = doc.get("fileName");
			String size = doc.get("size");
			String content = doc.get("content");
			String path1 = doc.get("path");
			System.out.println("文件名："+fileName);
			System.out.println("大小："+size);
			System.out.println("内容："+content);
			System.out.println("路径："+path1);
			System.out.println("------------------------");
		}
	}
	
	

}
