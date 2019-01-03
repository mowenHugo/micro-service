package com.hugo.search.util;

import com.hugo.search.constant.ResponseCode;
import com.hugo.search.exception.BaseException;
import com.hugo.search.model.Order;
import com.hugo.search.model.SimpleQuery;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ElasticsearchTemplate {
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchTemplate.class);
    private RestHighLevelClient client;
    private final String METHOD_PUT = "PUT";
    private final String METHOD_POST = "POST";

    public ElasticsearchTemplate(RestHighLevelClient client) {
        this.client = client;
    }


    public void createIndex(String index, String mapping) throws IOException {
        HttpEntity indexEntity = new NStringEntity(mapping, ContentType.APPLICATION_JSON);
        client.getLowLevelClient().performRequest(METHOD_PUT, "/" + index, new HashMap<String, String>(), indexEntity);
    }

    /*
     * 执行索引操作
     */
    public void indexRequest(IndexRequest request) throws IOException {
        if (StringUtils.isEmpty(request.id())) {
            logger.warn("index object id is null!");
            return;
        }
        GetRequest getRequest = new GetRequest(request.index(), request.type(), request.id());
        boolean exists = client.exists(getRequest);
        if (exists) {
            UpdateRequest updateRequest = new UpdateRequest(request.index(), request.type(), request.id());
            updateRequest.doc(request);
            updateRequest.retryOnConflict(3);
            UpdateResponse updateResponse = client.update(updateRequest);
            if(updateResponse != null && updateResponse.status() != null) {
                logger.info("UpdateResponse.status:"+ updateResponse.status().name());
            }
        } else {
            client.index(request);
        }
    }

    /**
     * 判断是否某个文档是否存在
     */
    public boolean exists(String index, String id) {
        GetRequest getRequest = null;
        if (!StringUtils.isEmpty(id)) {
            getRequest = new GetRequest(index, index, id);
        } else {
            getRequest = new GetRequest(index);
        }
        try {
            return client.exists(getRequest);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 删除某个索引
     */
    public void deleteIndexRequest(String index) {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        try {
//            client.indices().deleteIndex(request);  低版本api
            client.indices().delete(request);
        } catch (ElasticsearchStatusException e) {
            if (e.status().equals(RestStatus.NOT_FOUND)) {
                logger.warn(e.getDetailedMessage());
            } else {
                //实际业务中要自己封装异常类
                throw new RuntimeException("删除索引失败");
            }
        } catch (IOException e) {
            logger.error("delete index error. indexName:" + index, e);
            //实际业务中要自己封装异常类
            throw new RuntimeException("删除索引失败");
        }
    }

    /**
     * 删除索引数据
     */
    public void deleteRequest(String index, String type, String id) {
        DeleteRequest request = new DeleteRequest(index, type, id);
        try {
            client.delete(request);
        } catch (IOException e) {
            logger.error("delete index data error. indexName:" + index + ", id:" + id, e);
            throw new RuntimeException("删除索引数据异常");
        }
    }

    /**
     * 删除索引
     */
    public void deleteRequest(DeleteRequest deleteRequest) throws IOException {
        client.delete(deleteRequest);
    }

    /**
     * 更新索引文档
     */
    public void updateRequest(String index, String type, String id, Object data) throws IOException {
        UpdateRequest request = new UpdateRequest(index, type, id);
        if (data.getClass().isInstance(Map.class)) {
            request.doc(data);
        } else if (data.getClass().isInstance(String.class)) {
            request.doc(data, XContentType.JSON);
        }

        client.updateAsync(request, new ActionListener<UpdateResponse>() {
            @Override
            public void onResponse(UpdateResponse updateResponse) {
                logger.info("update success.");
            }

            @Override
            public void onFailure(Exception e) {
                logger.error("update fail.", e);
            }
        });
    }

    /**
     * 批量更新
     */
    public void blukRequest(BulkRequest request) throws IOException {
        BulkResponse response = client.bulk(request);
        if (response.hasFailures()) {
            String failureMessage = response.buildFailureMessage();
            logger.error("hasFailures : " + failureMessage);
            throw new BaseException(ResponseCode.BULK_INDEX_HAS_FAILURES);
        }
    }

    public void updateRequest(UpdateRequest updateRequest) throws IOException {
        client.update(updateRequest);
    }

    public Long count(SimpleQuery query) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(query.getIndexName());
        searchRequest.source(prepareSearch(query));
        try {
            SearchResponse searchResponse = client.search(searchRequest);
            return searchResponse.getHits().getTotalHits();
        } catch (IOException e) {
            logger.error("elasticsearch count error.", e);
            throw new BaseException(ResponseCode.SEARCH_ERROR);
        }
    }

    public SearchSourceBuilder prepareSearch(SimpleQuery query) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 设置包含与不包含字段
        sourceBuilder.fetchSource(query.getInclude(), query.getExclude());
        // 设置过滤条件
        if (null != query.getQueryBuilder()) {
            sourceBuilder.query(query.getQueryBuilder());
        }
        // 设置聚集条件
        if (null != query.getAggregationBuilder()) {
            sourceBuilder.aggregation(query.getAggregationBuilder());
        }
        if (!CollectionUtils.isEmpty(query.getAggregationBuilders())) {
            for (AggregationBuilder aggrBuilder : query.getAggregationBuilders()) {
                if (Objects.nonNull(aggrBuilder)) {
                    sourceBuilder.aggregation(aggrBuilder);
                }
            }
        }

        //设置分页条件
        if (null != query.getPage()) {
            int startRecord = 0;
            Integer page = query.getPage();
            if (page >= 1) {
                startRecord = (page - 1) * query.getPageSize();
            }
            sourceBuilder.from(startRecord);
            sourceBuilder.size(query.getPageSize());
        }
        // 设置排序条件
        if (null != query.getOrder()) {
            List<Order> orders = query.getOrder();
            for (Order order : orders) {
                if (order.getNestedPath() != null) {
                    sourceBuilder.sort(SortBuilders.fieldSort(order.getName()).order(SortOrder.valueOf(order.getType()))
                            .setNestedPath((order.getNestedPath())));
//                            .setNestedSort(new NestedSortBuilder(order.getNestedPath())));
                } else {
                    sourceBuilder.sort(order.getName(), SortOrder.valueOf(order.getType()));
                }
            }
        }
        return sourceBuilder;
    }

}
