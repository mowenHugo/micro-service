package com.hugo.search.model;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;

import java.util.List;

/**
 * 查询model
 */
public class SimpleQuery {

    /**
     * 索引名称
     */
    private String[] indexName;

    /**
     * 索引类别
     */
    private String[] indexType;

    /**
     * 查询包含范围
     */
    private String[] include;

    /**
     * 查询不包含范围
     */
    private String[] exclude;

    /**
     * 查询条件
     */
    private QueryBuilder queryBuilder;

    /**
     * 聚集条件
     */
    private AggregationBuilder aggregationBuilder;

    /**
     * 聚集条件集
     */
    private List<AggregationBuilder> aggregationBuilders;

    /**
     * 页数
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    /**
     * 排序
     */
    private List<Order> order;

    public String[] getIndexName() {
        return indexName;
    }

    public void setIndexName(String... indexName) {
        this.indexName = indexName;
    }

    public String[] getIndexType() {
        return indexType;
    }

    public void setIndexType(String... indexType) {
        this.indexType = indexType;
    }

    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public List<Order> getOrder() {
        return order;
    }

    public void setOrder(List<Order> order) {
        this.order = order;
    }

    public Integer getPage() {
        return page;
    }

    public String[] getInclude() {
        return include;
    }

    public void setInclude(String[] include) {
        this.include = include;
    }

    public String[] getExclude() {
        return exclude;
    }

    public void setExclude(String[] exclude) {
        this.exclude = exclude;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public AggregationBuilder getAggregationBuilder() {
        return aggregationBuilder;
    }

    public void setAggregationBuilder(AggregationBuilder aggregationBuilder) {
        this.aggregationBuilder = aggregationBuilder;
    }

    public List<AggregationBuilder> getAggregationBuilders() {
        return aggregationBuilders;
    }

    public void setAggregationBuilders(List<AggregationBuilder> aggregationBuilders) {
        this.aggregationBuilders = aggregationBuilders;
    }
}
