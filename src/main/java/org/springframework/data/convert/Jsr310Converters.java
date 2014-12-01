/*
 * Copyright 2013-2014 the original author or authors.
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
package org.springframework.data.convert;

import static java.time.Instant.*;
import static java.time.LocalDateTime.*;
import static java.time.ZoneId.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ClassUtils;

/**
 * Helper class to register JSR-310 specific {@link Converter} implementations in case the we're running on Java 8.
 * 
 * @author Oliver Gierke
 */
public abstract class Jsr310Converters {

	private static final boolean JAVA_8_IS_PRESENT = ClassUtils.isPresent("java.time.LocalDateTime",
			Jsr310Converters.class.getClassLoader());

	/**
	 * Returns the converters to be registered. Will only return converters in case we're running on Java 8.
	 * 
	 * @return
	 */
	public static Collection<Converter<?, ?>> getConvertersToRegister() {

		if (!JAVA_8_IS_PRESENT) {
			return Collections.emptySet();
		}

		List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();
		converters.add(DateToLocalDateTimeConverter.INSTANCE);
		converters.add(LocalDateTimeToDateConverter.INSTANCE);
		converters.add(DateToLocalDateConverter.INSTANCE);
		converters.add(LocalDateToDateConverter.INSTANCE);
		converters.add(DateToLocalTimeConverter.INSTANCE);
		converters.add(LocalTimeToDateConverter.INSTANCE);

		return converters;
	}

	static enum DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {

		INSTANCE;

		@Override
		public LocalDateTime convert(Date source) {
			return source == null ? null : ofInstant(source.toInstant(), systemDefault());
		}
	}

	static enum LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {

		INSTANCE;

		@Override
		public Date convert(LocalDateTime source) {
			return source == null ? null : Date.from(source.atZone(systemDefault()).toInstant());
		}
	}

	static enum DateToLocalDateConverter implements Converter<Date, LocalDate> {

		INSTANCE;

		@Override
		public LocalDate convert(Date source) {
			return source == null ? null : ofInstant(ofEpochMilli(source.getTime()), systemDefault()).toLocalDate();
		}
	}

	static enum LocalDateToDateConverter implements Converter<LocalDate, Date> {

		INSTANCE;

		@Override
		public Date convert(LocalDate source) {
			return source == null ? null : Date.from(source.atStartOfDay(systemDefault()).toInstant());
		}
	}

	static enum DateToLocalTimeConverter implements Converter<Date, LocalTime> {

		INSTANCE;

		@Override
		public LocalTime convert(Date source) {
			return source == null ? null : ofInstant(ofEpochMilli(source.getTime()), systemDefault()).toLocalTime();
		}
	}

	static enum LocalTimeToDateConverter implements Converter<LocalTime, Date> {

		INSTANCE;

		@Override
		public Date convert(LocalTime source) {
			return source == null ? null : Date.from(source.atDate(LocalDate.now()).atZone(systemDefault()).toInstant());
		}
	}
}