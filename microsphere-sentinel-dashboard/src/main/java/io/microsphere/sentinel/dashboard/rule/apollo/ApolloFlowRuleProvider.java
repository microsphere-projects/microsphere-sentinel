/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.microsphere.sentinel.dashboard.rule.apollo;

import io.microsphere.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import io.microsphere.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenNamespaceDTO;
import io.microsphere.sentinel.util.SentinelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hantianwei@gmail.com
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.5.0
 */
@Component("apolloFlowRuleProvider")
public class ApolloFlowRuleProvider implements DynamicRuleProvider<List<FlowRuleEntity>> {

    @Autowired
    private ApolloOpenConfig config;

    @Autowired
    private ApolloOpenApiClient apolloOpenApiClient;

    @Autowired
    private Converter<String, List<FlowRuleEntity>> converter;

    @Override
    public List<FlowRuleEntity> getRules(String appName) throws Exception {
        String appId = config.appId;
        String env = config.env;
        String clusterName = config.clusterName;
        String namespace = config.namespace;
        String flowDataId = SentinelUtils.getFlowDataId(appName);

        OpenNamespaceDTO openNamespaceDTO = apolloOpenApiClient.getNamespace(appId, env, clusterName, namespace);
        String rules = openNamespaceDTO
                .getItems()
                .stream()
                .filter(p -> p.getKey().equals(flowDataId))
                .map(OpenItemDTO::getValue)
                .findFirst()
                .orElse("");

        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return converter.convert(rules);
    }
}
