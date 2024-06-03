package com.querydslitemreader.core.pagingitemreader;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydslitemreader.core.pagingitemreader.options.QueryDslNoOffsetNumberOptions;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.function.Function;

public class QueryDslNoOffsetIdPagingItemReader<T, N extends Number & Comparable<?>> extends QueryDslPagingItemReader<T>{
    private QueryDslNoOffsetNumberOptions<T, N> options;

    private QueryDslNoOffsetIdPagingItemReader() {
        super();
        setName(ClassUtils.getShortName(QueryDslNoOffsetIdPagingItemReader.class));
    }

    public QueryDslNoOffsetIdPagingItemReader(EntityManagerFactory entityManagerFactory,
                                              int pageSize,
                                              QueryDslNoOffsetNumberOptions<T, N> options,
                                              Function<JPAQueryFactory, JPAQuery<T>> queryFunction) {
        super(entityManagerFactory, pageSize, queryFunction);
        setName(ClassUtils.getShortName(QueryDslNoOffsetIdPagingItemReader.class));
        this.options = options;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doReadPage() {

        JPQLQuery<T> query = createQuery().limit(getPageSize());

        initResults();

        fetchQuery(query);

        resetCurrentIdIfNotLastPage();
    }

    @Override
    protected JPAQuery<T> createQuery() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        JPAQuery<T> query = queryFunction.apply(queryFactory);
        options.initKeys(query, getPage()); // 제일 첫번째 페이징시 시작해야할 ID 찾기

        return options.createQuery(query, getPage());
    }

    private void resetCurrentIdIfNotLastPage() {
        if (isNotEmptyResults()) {
            options.resetCurrentId(getLastItem());
        }
    }

    // 조회결과가 Empty이면 results에 null이 담긴다
    private boolean isNotEmptyResults() {
        return !CollectionUtils.isEmpty(results) && results.get(0) != null;
    }

    private T getLastItem() {
        return results.get(results.size() - 1);
    }}
