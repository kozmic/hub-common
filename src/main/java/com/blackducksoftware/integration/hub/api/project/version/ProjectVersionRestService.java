/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.blackducksoftware.integration.hub.api.project.version;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.restlet.data.Method;

import com.blackducksoftware.integration.hub.api.HubItemRestService;
import com.blackducksoftware.integration.hub.api.HubRequest;
import com.blackducksoftware.integration.hub.api.project.ProjectItem;
import com.blackducksoftware.integration.hub.exception.BDRestException;
import com.blackducksoftware.integration.hub.exception.UnexpectedHubResponseException;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class ProjectVersionRestService extends HubItemRestService<ProjectVersionItem> {
    private static final Type ITEM_TYPE = new TypeToken<ProjectVersionItem>() {
    }.getType();

    private static final Type ITEM_LIST_TYPE = new TypeToken<List<ProjectVersionItem>>() {
    }.getType();

    public ProjectVersionRestService(final RestConnection restConnection, final Gson gson,
            final JsonParser jsonParser) {
        super(restConnection, gson, jsonParser, ITEM_TYPE, ITEM_LIST_TYPE);
    }

    public ProjectVersionItem getProjectVersion(ProjectItem project, String projectVersionName)
            throws UnexpectedHubResponseException, IOException, URISyntaxException, BDRestException {
        final HubRequest projectVersionItemRequest = createDefaultHubRequest(project);
        addProjectVersionNameQuery(projectVersionItemRequest, projectVersionName);

        final JsonObject jsonObject = projectVersionItemRequest.executeForResponseJson();
        final List<ProjectVersionItem> allProjectVersionMatchingItems = getAll(jsonObject, projectVersionItemRequest);
        for (ProjectVersionItem projectVersion : allProjectVersionMatchingItems) {
            if (projectVersionName.equals(projectVersion.getVersionName())) {
                return projectVersion;
            }
        }

        throw new UnexpectedHubResponseException(String.format("Could not find the version: %s for project: %s", projectVersionName, project.getName()));
    }

    public List<ProjectVersionItem> getAllProjectVersions(final ProjectItem project)
            throws UnexpectedHubResponseException, IOException, URISyntaxException, BDRestException {
        String versionsUrl = getVersionsUrl(project);
        return getAllProjectVersions(versionsUrl);
    }

    public List<ProjectVersionItem> getAllProjectVersions(final String versionsUrl)
            throws IOException, URISyntaxException, BDRestException {
        HubRequest projectVersionItemRequest = createDefaultHubRequest(versionsUrl);

        final JsonObject jsonObject = projectVersionItemRequest.executeForResponseJson();
        final List<ProjectVersionItem> allProjectVersionItems = getAll(jsonObject, projectVersionItemRequest);
        return allProjectVersionItems;
    }

    public String getVersionsUrl(ProjectItem project) throws UnexpectedHubResponseException {
        String versionsUrl = project.getLink(ProjectItem.VERSION_LINK);
        return versionsUrl;
    }

    private HubRequest createDefaultHubRequest(ProjectItem project) throws UnexpectedHubResponseException {
        return createDefaultHubRequest(getVersionsUrl(project));
    }

    private HubRequest createDefaultHubRequest(String versionsUrl) {
        final HubRequest projectVersionItemRequest = new HubRequest(getRestConnection(), getJsonParser());

        projectVersionItemRequest.setMethod(Method.GET);
        projectVersionItemRequest.setLimit(100);
        projectVersionItemRequest.setUrl(versionsUrl);

        return projectVersionItemRequest;
    }

    private void addProjectVersionNameQuery(HubRequest projectVersionItemRequest, String projectVersionName) {
        if (StringUtils.isNotBlank(projectVersionName)) {
            projectVersionItemRequest.setQ(String.format("versionName:%s", projectVersionName));
        }
    }

}
