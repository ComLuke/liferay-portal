package com.liferay.dynamic.data.mapping.data.source.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.liferay.dynamic.data.mapping.data.source.DDMDataSource;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.collections.ServiceReferenceMapper;

public class DDMDataSourceServiceReferenceMapper<T extends DDMDataSource>
	implements ServiceReferenceMapper<String, T> {

	public static final <S extends DDMDataSource>
		DDMDataSourceServiceReferenceMapper<S> create() {

		return new DDMDataSourceServiceReferenceMapper<>();
	}

	@Override
	public void map(
		ServiceReference<T> serviceReference, Emitter<String> emitter) {

		String sourceType = (String)serviceReference.getProperty(
			"sourceType");

		if (Validator.isNull(sourceType)) {
			_log.error(
				"Unable to register DDM Data Source service because of "
				+ "missing service property \"sourceType\"");
		}
		else {
			emitter.emit(sourceType);
		}
	}

	private static final Log _log = LogFactory.getLog(
		DDMDataSourceServiceReferenceMapper.class);

}