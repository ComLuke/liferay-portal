/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.mapping.data.source.impl;

import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.dynamic.data.mapping.data.source.DDMDataSource;
import com.liferay.dynamic.data.mapping.data.source.DDMDataSourceRegistry;
import com.liferay.dynamic.data.mapping.data.source.util.DDMDataSourceServiceReferenceMapper;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.registry.collections.ServiceTrackerCollections;
import com.liferay.registry.collections.ServiceTrackerMap;

/**
 * @author Luca Comin
 */
@Component(
	immediate = true, service = DDMDataSourceRegistry.class
)
public class DDMDataSourceRegistryImpl implements DDMDataSourceRegistry {

	@Override
	public String[] getSourceTypes() {
		return ArrayUtil.toStringArray(_serviceTrackerMap.keySet());
	}

	@Override
	public DDMDataSource getDataSource(String sourceType) {
		List<DDMDataSource> dataSources = _serviceTrackerMap.getService(
			sourceType);

		if (dataSources != null && !dataSources.isEmpty()) {
			return dataSources.get(0);
		}

		return null;
	}

	public DDMDataSourceRegistryImpl() {
		_serviceTrackerMap.open();
	}

	private static final ServiceTrackerMap<String, List<DDMDataSource>>
		_serviceTrackerMap = ServiceTrackerCollections.multiValueMap(
			DDMDataSource.class, "(sourceType=*)",
			DDMDataSourceServiceReferenceMapper.<DDMDataSource>create());

}