/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.microsphere.sentinel.dashboard.discovery;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Cloud {@link MachineDiscovery}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see MachineDiscovery
 * @since 1.0.0
 */
@Component
public class CloudMachineDiscovery implements MachineDiscovery {

    private final DiscoveryClient discoveryClient;

    public CloudMachineDiscovery(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public List<String> getAppNames() {
        return discoveryClient.getServices()
                .stream().filter(this::isValidServices)
                .collect(Collectors.toList());
    }

    @Override
    public Set<AppInfo> getBriefApps() {
        return getAppNames()
                .stream()
                .map(AppInfo::new)
                .collect(Collectors.toSet());
    }

    @Override
    public AppInfo getDetailApp(String app) {
        AppInfo appInfo = new AppInfo();
        appInfo.setApp(app);
        List<ServiceInstance> instances = discoveryClient.getInstances(app);
        instances.stream()
                .map(this::createMachineInfo)
                .forEach(appInfo::addMachine);
        return appInfo;
    }

    @Override
    public void removeApp(String app) {
        // DO NOTHING
    }

    @Override
    public long addMachine(MachineInfo machineInfo) {
        // DO NOTHING
        return 0;
    }

    @Override
    public boolean removeMachine(String app, String ip, int port) {
        // DO NOTHING
        return false;
    }

    private MachineInfo createMachineInfo(ServiceInstance serviceInstance) {
        MachineInfo machineInfo = new MachineInfo();
        Map<String, String> metadata = serviceInstance.getMetadata();
        machineInfo.setApp(serviceInstance.getServiceId());
        machineInfo.setHostname(metadata.get("host.name"));
        machineInfo.setIp(serviceInstance.getHost());
        machineInfo.setPort(serviceInstance.getPort());
        machineInfo.setVersion(metadata.get("sentinel.version"));
        return machineInfo;
    }

    private boolean isValidServices(String serviceId) {
        // Filter Dubbo Service Prefix
        return !serviceId.startsWith("consumers:")
                && !serviceId.startsWith("providers:");
    }
}
