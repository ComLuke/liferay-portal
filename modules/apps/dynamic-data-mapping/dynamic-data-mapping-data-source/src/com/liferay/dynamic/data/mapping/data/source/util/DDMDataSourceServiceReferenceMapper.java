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

package com.liferay.dynamic.data.mapping.data.source.util;

import com.liferay.dynamic.data.mapping.data.source.DDMDataSource;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.collections.ServiceReferenceMapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Luca Comin
 */
public class DDMDataSourceServiceReferenceMapper<T extends DDMDataSource>
	implements ServiceReferenceMapper<String, T> {

	public static final <S extends DDMDataSource>
		DDMDataSourceServiceReferenceMapper<S> create() {

		return new DDMDataSourceServiceReferenceMapper<>();
	}

	@Override
	public void map(
		ServiceReference<T> serviceReference, Emitter<String> emitter) {

		String sourceType = (String)serviceReference.getProperty("sourceType");

		if (Validator.isNull(sourceType)) {
			_log.error(
				"Unable to register DDM Data Source service because of " +
				"missing service property \"sourceType\"");
		}
		else {
			emitter.emit(sourceType);
		}
	}

	private static final Log _log = LogFactory.getLog(
		DDMDataSourceServiceReferenceMapper.class);

}