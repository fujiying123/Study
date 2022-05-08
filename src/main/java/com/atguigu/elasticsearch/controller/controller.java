package com.atguigu.elasticsearch.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.elasticsearch.common.ResponseResult;
import com.atguigu.elasticsearch.entities.Man;
import com.atguigu.elasticsearch.entities.Person;
import com.atguigu.elasticsearch.entities.User;
import com.atguigu.elasticsearch.service.RefService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.AliasMetadata;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.LongBounds;
import org.elasticsearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

/**
 * @Description：
 * @Date： 2021/8/16
 * @Author：小影
 */
@RestController
@Slf4j
public class controller {


    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private RefService refService;


    /**
     * 创建索引
     * @return
     */
    @GetMapping("/create")
    public ResponseEntity<Map<String,Object>> createIndex() {
        CreateIndexRequest request = new CreateIndexRequest("user");
        try {
            CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            boolean acknowledged = response.isAcknowledged();
            restHighLevelClient.close();
            return ResponseResult.createResult(acknowledged);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询索引
     * @return
     */
    @GetMapping("/index")
    public ResponseEntity<Map<String,Object>> getIndex(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest(index);
        GetIndexResponse response = restHighLevelClient.indices().get(request, RequestOptions.DEFAULT);
        System.out.println(response.getAliases());
        System.out.println(response.getMappings());
        System.out.println(response.getSettings());

        return ResponseResult.createResult(response);

    }

    /**
     * 删除索引
     * @return
     */
    @DeleteMapping("/index")
    public ResponseEntity<Map<String,Object>> deleteIndex(String index) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
       restHighLevelClient.close();
        boolean acknowledged = response.isAcknowledged();

        return ResponseResult.createResult(acknowledged);

    }

    /**
     * 存数据
     * @return
     */
    @PostMapping("/index")
    public ResponseEntity<Map<String,Object>> createData() throws IOException {
        IndexRequest request = new IndexRequest();
        request.index("user").id("110");
        User user = new User();
        user.setName("张三");
        user.setAge("18");
        user.setSex("男");

        String userString = JSONObject.toJSONString(user);
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(user);
        request.source(userString, XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
//        restHighLevelClient.close();
        DocWriteResponse.Result result = response.getResult();
        System.out.println("========"+response.getResult());
        return ResponseResult.createResult();

    }

    /**
     * 修改数据
     * @return
     */
    @PutMapping("/index")
    public ResponseEntity<Map<String,Object>> updateData() throws IOException {
        UpdateRequest request = new UpdateRequest();
        request.index("user").id("110");
        request.doc(XContentType.JSON,"sex","女");


        UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
//        restHighLevelClient.close();
        DocWriteResponse.Result result = response.getResult();
        System.out.println("========"+response.getResult());
        return ResponseResult.createResult();

    }

    /**
     * 查询数据
     * @return
     */
    @GetMapping("/data")
    public ResponseEntity<Map<String,Object>> getData() throws IOException {
        GetRequest request = new GetRequest();
        request.index("user").id("110");
        SearchRequest request1 = new SearchRequest();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        SearchSourceBuilder builder = new SearchSourceBuilder();

        GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);

        String sourceAsString = response.getSourceAsString();
        System.out.println("========"+sourceAsString);
        return ResponseResult.createResult(sourceAsString);

    }

    /**
     * 查询数据
     * @return
     */
    @DeleteMapping("/data")
    public ResponseEntity<Map<String,Object>> deleteData() throws IOException {
        DeleteRequest request = new DeleteRequest();
        request.index("user").id("110");

        DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);

        DocWriteResponse.Result result = response.getResult();
        System.out.println("========"+result);
        return ResponseResult.createResult(request.toString());

    }


    /**
     * 批量新增数据
     * @return
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String,Object>> batchData() throws IOException {
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest().index("user").id("111").source(XContentType.JSON,"name","张三","age",30,"sex","男"));
        request.add(new IndexRequest().index("user").id("112").source(XContentType.JSON,"name","里斯","age",35,"sex","男"));
        request.add(new IndexRequest().index("user").id("113").source(XContentType.JSON,"name","王五","age",40,"sex","男"));
        request.add(new IndexRequest().index("user").id("114").source(XContentType.JSON,"name","狗蛋","age",45,"sex","男"));
        request.add(new IndexRequest().index("user").id("115").source(XContentType.JSON,"name","啊猫","age",50,"sex","男"));

        BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);

        BulkItemResponse[] items = response.getItems();
        System.out.println("========"+items);
        return ResponseResult.createResult(response.toString());

    }

    /**
     * 批量新增数据
     * @return
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String,Object>> batchDelete() throws IOException {
        BulkRequest request = new BulkRequest();
        request.add(new DeleteRequest().index("user").id("111"));
        request.add(new DeleteRequest().index("user").id("112"));
        request.add(new DeleteRequest().index("user").id("113"));


        BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);

        BulkItemResponse[] items = response.getItems();
        System.out.println("========"+items);
        return ResponseResult.createResult(response.toString());

    }


    /**
     * 查询索引所有数据
     * @return
     */
    @GetMapping("/batch")
    public ResponseEntity<Map<String,Object>> getBatch() throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices("user");
        request.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));

        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        System.out.println("========"+search.getHits());
        return ResponseResult.createResult(search.getHits().getHits());

    }

    /**
     * 条件查询
     * @return
     */
    @GetMapping("/term")
    public ResponseEntity<Map<String,Object>> getTerm() throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices("user");
        request.source(new SearchSourceBuilder().query(QueryBuilders.termQuery("name","张")));

        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        System.out.println("========"+search.getHits());
        return ResponseResult.createResult(search.getHits().getHits());

    }

    /**
     * 分页查询
     * @return
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String,Object>> getPage() throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices("user");
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
//        builder.from(0);
//        builder.size(3);
        builder.sort("name", SortOrder.DESC);
        request.source(builder);

        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        System.out.println("========"+search.getHits());
        return ResponseResult.createResult(search.getHits().getHits());

    }

    /**
     * 过滤查询
     * @return
     */
    @GetMapping("/filter")
    public ResponseEntity<Map<String,Object>> filter() throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices("user");
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
        String[] exclude = {};
        String[] include = {"name"};
        builder.fetchSource(include,exclude);
        request.source(builder);

        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        System.out.println("========"+search.getHits());
        return ResponseResult.createResult(search.getHits().getHits());

    }


    /**
     * 组合查询
     * @return
     */
    @GetMapping("/combination")
    public ResponseEntity<Map<String,Object>> combination() throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices("user");
        SearchSourceBuilder builder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.must(QueryBuilders.matchQuery("age",30));
//        boolQueryBuilder.must(QueryBuilders.matchQuery("sex","男"));
        boolQueryBuilder.should(QueryBuilders.matchQuery("age",30));
        boolQueryBuilder.should(QueryBuilders.matchQuery("age",40));
        builder.query(boolQueryBuilder);
        request.source(builder);

        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        System.out.println("========"+search.getHits());
        return ResponseResult.createResult(search.getHits().getHits());

    }


    /**
     * 范围查询
     * @return
     */
    @GetMapping("/range")
    public ResponseEntity<Map<String,Object>> range() throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices("user");
        SearchSourceBuilder builder = new SearchSourceBuilder();

        RangeQueryBuilder range = QueryBuilders.rangeQuery("age");
        range.gte(30);
        range.lte(40);

        builder.query(range);
        request.source(builder);

        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        System.out.println("========"+search.getHits());
        return ResponseResult.createResult(search.getHits().getHits());

    }

    /**
     * 模糊查询
     * @return
     */
    @GetMapping("/like")
    public ResponseEntity<Map<String,Object>> like() throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices("user");
        SearchSourceBuilder builder = new SearchSourceBuilder();

        //Fuzziness.ONE匹配差一个字符
        FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("name", "wangwu").fuzziness(Fuzziness.ONE);


        builder.query(fuzzyQueryBuilder);
        request.source(builder);

        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        System.out.println("========"+search.getHits());
        return ResponseResult.createResult(search.getHits().getHits());

    }


 /**
     * 聚合查询
     * @return
     */
    @GetMapping("/polymerization")
    public ResponseEntity<Map<String,Object>> polymerization() throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices("user");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        AggregationBuilder aggregationBuilder  = AggregationBuilders.max("maxId").field("name");
        AggregationBuilders.dateHistogram("days").field("date").format("yyyy-MM-dd").timeZone(ZoneId.of("Asia/Shanghai")).
                minDocCount(0).fixedInterval(DateHistogramInterval.DAY).extendedBounds(new LongBounds("2022-01-01 00:00:00","2022-01-31 59:59:59"));

        builder.aggregation(aggregationBuilder);
        request.source(builder);

        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        System.out.println("========"+search.getHits());
        return ResponseResult.createResult(search.getHits().getHits());

    }

    /**
     * 聚合查询
     * @return
     */
    @GetMapping("/group")
    public ResponseEntity<Map<String,Object>> group() throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices("user");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        AggregationBuilder aggregationBuilder  = AggregationBuilders.terms("ageGroup").field("age");


        builder.aggregation(aggregationBuilder);
        request.source(builder);

        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        System.out.println("========"+search.getHits());
        return ResponseResult.createResult(search.getHits().getHits());

    }

    public static void main(String[] args) {
        Integer i =158320;
    }

    @Autowired
    private ApplicationContext context;


    @GetMapping("/ref")
    public void ref() throws Exception {
//        refService.ref();
        XStream xStream = new XStream(new DomDriver());
        xStream.allowTypes(new Class[]{Person.class, Man.class});
        xStream.processAnnotations(Person.class);
        xStream.processAnnotations(Man.class);
        Resource[] resources = context.getResources("classpath:protocol/**/*.xml");
        for (Resource resource : resources) {
            InputStream inputStream = resource.getInputStream();
            Person person = new Person();
            xStream.fromXML(inputStream,person);
            log.info("name={}",person.getName());
        }
        Person bean = (Person)context.getBean("build:119");
        log.info("age={}",bean.getAge());
    }

    public void test() {
        refService.eat();
    }



}
