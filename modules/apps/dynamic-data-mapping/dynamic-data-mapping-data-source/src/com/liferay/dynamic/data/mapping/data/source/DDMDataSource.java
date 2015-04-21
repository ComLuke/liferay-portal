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

package com.liferay.dynamic.data.mapping.data.source;

import java.util.List;
import java.util.Locale;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.KeyValuePair;

/**
 * @author Luca Comin
 */
public interface DDMDataSource {

	public String getType();

	public List<KeyValuePair> getData(
			String keywords, long ddmStructureId, String fieldName,
			Locale locale)
		throws Exception;

	public List<KeyValuePair> getData(
			String keywords, long ddmStructureId, String fieldName,
			Locale locale, boolean like, boolean andSearch, int start, int end)
		throws Exception;

}