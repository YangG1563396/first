package com.itheima.solr;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

public class SolrManager {
	
	
	//solr测试查询
	@Test
	public void testQuery() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://localhost:80/solr/collection1");
		SolrQuery solrQuery=new SolrQuery();
		solrQuery.set("q", "花儿");
		solrQuery.set("df", "product_keywords");
		solrQuery.addFilterQuery("product_price:[* TO 20]");
		solrQuery.setStart(0);
		solrQuery.setRows(10);
		solrQuery.addSort("product_price",ORDER.desc);
		
		solrQuery.setHighlight(true);
		solrQuery.setHighlightSimplePre("<span style=\"color:red\">");
		solrQuery.setHighlightSimplePost("</span>");
		solrQuery.addHighlightField("product_name");
		
		QueryResponse queryResponse = solrServer.query(solrQuery);
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
		
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		System.out.println("总条数："+solrDocumentList.getNumFound());
		for (SolrDocument solrDocument : solrDocumentList) {
			Map<String, List<String>> map = highlighting.get(solrDocument.get("id"));
			List<String> list = map.get("product_name");
			String product_name="";
			if(list!=null&&list.size()>0) {
				product_name=list.get(0);
			}else {
				product_name=(String) solrDocument.get("product_name");
			}
			System.out.println(solrDocument.get("id"));
			System.out.println(product_name);
			System.out.println(solrDocument.get("product_price"));
			System.out.println(solrDocument.get("product_picture"));
			System.out.println(solrDocument.get("product_catalog_name"));
			System.out.println("-------------------");
		}
		
	}
	
	@Test
	public void test() {
		System.out.println("你少来");
		System.out.println("臭屁");
	}

}
