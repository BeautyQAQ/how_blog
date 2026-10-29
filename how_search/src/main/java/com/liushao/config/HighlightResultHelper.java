package com.liushao.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.beanutils.PropertyUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * ElasticSearch高亮配置
 */
@Slf4j
@Component
public class HighlightResultHelper implements SearchResultMapper {

    private final ObjectMapper objectMapper;

    public HighlightResultHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
        List<T> results = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            if (hit != null) {
                T result = null;
                if (StringUtils.hasText(hit.getSourceAsString())) {
                    result = readSource(hit.getSourceAsString(), clazz);
                }
                // 高亮查询
                for (HighlightField field : hit.getHighlightFields().values()) {
                    try {
                        PropertyUtils.setProperty(result, field.getName(), concat(field.fragments()));
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        log.error("设置高亮字段异常：{}", e.getMessage(), e);
                    }
                }
                results.add(result);
            }
        }
        return new AggregatedPageImpl<T>(results, pageable, response.getHits().getTotalHits(), response
                .getAggregations(), response.getScrollId());
    }

    @Override
    public <T> T mapSearchHit(SearchHit searchHit, Class<T> clazz) {
        List<T> results = new ArrayList<>();
        for (HighlightField field : searchHit.getHighlightFields().values()) {
            T result = null;
            if (StringUtils.hasText(searchHit.getSourceAsString())) {
                result = readSource(searchHit.getSourceAsString(), clazz);
            }
            try {
                PropertyUtils.setProperty(result, field.getName(), concat(field.fragments()));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error("设置高亮字段异常：{}", e.getMessage(), e);
            }
            results.add(result);
        }
        return null;
    }

    private <T> T readSource(String source, Class<T> clazz) {
        try {
            return objectMapper.readerFor(clazz)
                    .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(source);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot deserialize Elasticsearch hit as " + clazz.getName(), e);
        }
    }

    private String concat(Text[] texts) {
        StringBuffer sb = new StringBuffer();
        for (Text text : texts) {
            sb.append(text.toString());
        }
        return sb.toString();
    }

}
