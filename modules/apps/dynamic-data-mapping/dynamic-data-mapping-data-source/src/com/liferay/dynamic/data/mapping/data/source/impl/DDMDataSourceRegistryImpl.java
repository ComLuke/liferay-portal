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

import java.util.Collection;

import org.osgi.service.component.annotations.Component;

import com.liferay.dynamic.data.mapping.data.source.DDMDataSource;
import com.liferay.dynamic.data.mapping.data.source.DDMDataSourceRegistry;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;

/**
 * @author Luca Comin
 */

@Component(
	immediate = true, service = DDMDataSourceRegistry.class
)
public class DDMDataSourceRegistryImpl implements DDMDataSourceRegistry {

	@Override
	public DDMDataSource getDataSource(String sourceType) {
		Registry registry = RegistryUtil.getRegistry();

		try {
			Collection<DDMDataSource> dataSources = registry.getServices(
				DDMDataSource.class, "(sourceType=" + sourceType + ")");

			if (!dataSources.isEmpty()) {
				return dataSources.iterator().next();
			}
		}
		catch (Exception e) {
			return null;
		}

		return null;
	}

}