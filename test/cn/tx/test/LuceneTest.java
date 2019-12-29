package cn.tx.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void importIndex() throws IOException {
		
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
		//采集原始文档
		File sourceFile = new File("E:\\searchsource");
		//获得文件夹下的所有文件
		File [] files = sourceFile.listFiles();
		//遍历每一个文件
		for(File file : files){
			//获得文件的属性
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
			//把文档写入到索引库
			iw.addDocument(doc);
		}
		//提交
		iw.commit();
		iw.close();
		
	}

}
