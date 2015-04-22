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

import com.liferay.dynamic.data.mapping.data.source.DDMDataSource;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.BooleanQueryFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.dynamicdatalists.model.DDLRecord;
import com.liferay.portlet.dynamicdatalists.model.DDLRecordSet;
import com.liferay.portlet.dynamicdatalists.service.DDLRecordLocalServiceUtil;
import com.liferay.portlet.dynamicdatalists.service.DDLRecordSetLocalServiceUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.render.DDMFormFieldValueRenderer;
import com.liferay.portlet.dynamicdatamapping.render.DDMFormFieldValueRendererRegistryUtil;
import com.liferay.portlet.dynamicdatamapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portlet.dynamicdatamapping.util.DDMIndexerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Luca Comin
 */
@Component(
	immediate = true, property = {"sourceType=ddl"},
	service = DDMDataSource.class
)
public class DDMDataSourceImpl implements DDMDataSource {

	@Override
	public List<KeyValuePair> getData(
			String keywords, long ddmStructureId, String fieldName,
			Locale locale)
		throws Exception {

		return getData(
			keywords, ddmStructureId, fieldName, locale, true, false,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<KeyValuePair> getData(
			String keywords, long ddmStructureId, String fieldName,
			Locale locale, boolean like, boolean andSearch, int start, int end)
		throws Exception {

		DDMStructure structure = DDMStructureLocalServiceUtil.getDDMStructure(
			ddmStructureId);

		String sourceDDLRecordSetId = structure.getFieldProperty(
			fieldName, _SOURCE_DDL_RECORD_SET_ID);
		String sourceDDLKeyFieldName = structure.getFieldProperty(
			fieldName, _SOURCE_DDL_KEY_FIELD_NAME);
		String sourceDDLValueFieldName = structure.getFieldProperty(
			fieldName, _SOURCE_DDL_VALUE_FIELD_NAME);

		DDLRecordSet recordSet = DDLRecordSetLocalServiceUtil.getRecordSet(
			structure.getGroupId(), sourceDDLRecordSetId);

		long sourceDDMStructureId = recordSet.getDDMStructureId();

		String keyFieldName = DDMIndexerUtil.encodeName(
			sourceDDMStructureId, sourceDDLKeyFieldName, locale);

		SearchContext searchContext = new SearchContext();

		searchContext.setAndSearch(andSearch);
		searchContext.setCompanyId(recordSet.getCompanyId());
		searchContext.setStart(start);
		searchContext.setEnd(end);

		searchContext.setAttribute("recordSetId", recordSet.getRecordSetId());
		searchContext.setAttribute("paginationType", "none");

		BooleanQuery booleanQuery = BooleanQueryFactoryUtil.create(
			searchContext);

		BooleanClauseOccur occur;

		if (andSearch) {
			occur = BooleanClauseOccur.MUST;
		}
		else {
			occur = BooleanClauseOccur.SHOULD;
		}

		if (Validator.isNotNull(keywords)) {
			BooleanQuery keyQuery = BooleanQueryFactoryUtil.create(
				searchContext);

			keyQuery.addTerm(keyFieldName, keywords, like);

			booleanQuery.add(keyQuery, occur);
		}

		BooleanClause booleanClause = BooleanClauseFactoryUtil.create(
			searchContext, booleanQuery, BooleanClauseOccur.MUST.getName());

		searchContext.setBooleanClauses(new BooleanClause[] {booleanClause});

		QueryConfig queryConfig = new QueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setLocale(locale);
		queryConfig.setScoreEnabled(false);
		queryConfig.setAttribute("defaultOperator", "AND_OPERATOR");

		searchContext.setQueryConfig(queryConfig);

		Hits hits = DDLRecordLocalServiceUtil.search(searchContext);

		List<Document> documents = hits.toList();

		List<KeyValuePair> results = new ArrayList<>();

		for (Document document : documents) {
			long ddlRecordId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			DDLRecord ddlRecord = DDLRecordLocalServiceUtil.getDDLRecord(
				ddlRecordId);

			DDMFormFieldValueRenderer ddmFormKeyFieldRenderer =
				DDMFormFieldValueRendererRegistryUtil.
					getDDMFormFieldValueRenderer(
						(String)ddlRecord.getFieldType(sourceDDLKeyFieldName));

			DDMFormFieldValueRenderer ddmFormValueFieldRenderer =
				DDMFormFieldValueRendererRegistryUtil.
					getDDMFormFieldValueRenderer(
						(String)ddlRecord.getFieldType(
							sourceDDLValueFieldName));

			String keyFieldValue = ddmFormKeyFieldRenderer.render(
				ddlRecord.getDDMFormFieldValues(keyFieldName), locale);

			String valueFieldValue = ddmFormValueFieldRenderer.render(
				ddlRecord.getDDMFormFieldValues(keyFieldName), locale);

			results.add(new KeyValuePair(keyFieldValue, valueFieldValue));
		}

		return results;
	}

	public String getType() {
		return _TYPE;
	}

	private static final String _SOURCE_DDL_KEY_FIELD_NAME =
		"sourceKeyFieldName";

	private static final String _SOURCE_DDL_RECORD_SET_ID = "sourceRecordSetId";

	private static final String _SOURCE_DDL_VALUE_FIELD_NAME =
		"sourceValueFieldName";

	private static final String _TYPE = "ddl";

}