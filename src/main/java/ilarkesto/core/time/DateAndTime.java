/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.core.time;

import ilarkesto.core.base.Args;
import ilarkesto.core.base.Str;
import ilarkesto.core.base.Str.Formatable;

import java.io.Serializable;

public class DateAndTime implements Comparable<DateAndTime>, Serializable, Formatable {

	protected Date date;
	protected Time time;

	private transient int hashCode;

	public DateAndTime(java.util.Date javaDate) {
		this(new Date(javaDate), new Time(javaDate));
	}

	public DateAndTime(String dateAndTimeString) {
		Args.assertNotNull(dateAndTimeString, "dateAndTimeString");
		dateAndTimeString = dateAndTimeString.trim();
		int idx = dateAndTimeString.indexOf(' ');

		if (idx > 0) {
			String sDate = dateAndTimeString.substring(0, idx);
			String sTime = dateAndTimeString.substring(idx + 1);
			date = new Date(sDate);
			time = new Time(sTime);
		} else {
			if (dateAndTimeString.indexOf('.') > 0) {
				date = new Date(dateAndTimeString);
				time = new Time("0");
			} else {
				date = Date.today();
				time = new Time(dateAndTimeString);
			}
		}
	}

	public DateAndTime(Date date, Time time) {
		Args.assertNotNull(date, "date", time, "time");
		this.date = date;
		this.time = time;
	}

	public DateAndTime(int year, int month, int day, int hour, int minute, int second) {
		this(new Date(year, month, day), new Time(hour, minute, second));
	}

	public DateAndTime(long millis) {
		this(Tm.createDate(millis));
	}

	public DateAndTime() {
		this(Tm.getNowAsDate());
	}

	// ---

	public DateAndTime addDays(int days) {
		return new DateAndTime(Tm.addDays(toJavaDate(), days));
	}

	public DateAndTime addHours(int hours) {
		return new DateAndTime(toMillis() + (hours * Tm.HOUR));
	}

	public DateAndTime addMinutes(int minutes) {
		return new DateAndTime(toMillis() + (minutes * Tm.MINUTE));
	}

	public DateAndTime addSeconds(int seconds) {
		return new DateAndTime(toMillis() + (seconds * Tm.SECOND));
	}

	public TimePeriod getPeriodTo(DateAndTime other) {
		return new TimePeriod(other.toMillis() - toMillis());
	}

	public TimePeriod getPeriodToNow() {
		return getPeriodTo(now());
	}

	public TimePeriod getPeriodFromNow() {
		return now().getPeriodTo(this);
	}

	public final boolean isPast() {
		return isBefore(now());
	}

	public final boolean isFuture() {
		return isAfter(now());
	}

	public final boolean isBefore(Date other) {
		return getDate().isBefore(other);
	}

	public final boolean isBefore(DateAndTime other) {
		return compareTo(other) < 0;
	}

	public boolean isBeforeOrSame(DateAndTime other) {
		return compareTo(other) <= 0;
	}

	public final boolean isAfter(Date other) {
		return getDate().isAfter(other);
	}

	public final boolean isAfter(DateAndTime other) {
		return compareTo(other) > 0;
	}

	public boolean isAfterOrSame(DateAndTime other) {
		return compareTo(other) >= 0;
	}

	public final Date getDate() {
		return date;
	}

	public final Time getTime() {
		return time;
	}

	public final java.util.Date toJavaDate() {
		return Tm.createDate(date.toMillis() + time.toMillis());
	}

	public final long toMillis() {
		return date.toMillis() + time.toMillis();
	}

	public String formatLog() {
		return date.formatYearMonthDay() + "_" + time.formatLog();
	}

	public String formatDayMonthYearHourMinute() {
		return date.formatDayMonthYear() + " " + time.formatHourMinute();
	}

	public String formatYearMonthDayHourMinute() {
		return date.formatYearMonthDay() + " " + time.formatHourMinute();
	}

	@Override
	public String format() {
		return date.format() + " " + time.format();
	}

	@Override
	public final String toString() {
		return date + " " + time;
	}

	@Override
	public final int hashCode() {
		if (hashCode == 0) {
			hashCode = 23;
			hashCode = hashCode * 37 + date.hashCode();
			hashCode = hashCode * 37 + time.hashCode();
		}
		return hashCode;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof DateAndTime)) return false;
		return toMillis() == ((DateAndTime) obj).toMillis();
	}

	public final boolean equalsIgnoreSeconds(DateAndTime other) {
		if (!date.equals(other.date)) return false;
		return time.equalsIgnoreSeconds(other.time);
	}

	@Override
	public final int compareTo(DateAndTime o) {
		long a = toMillis();
		long b = o.toMillis();
		if (a > b) return 1;
		if (a < b) return -1;
		return 0;
	}

	// --- static ---

	public static DateAndTime now() {
		return new DateAndTime();
	}

	public static DateAndTime latest(Iterable<DateAndTime> dates) {
		DateAndTime latest = null;
		for (DateAndTime date : dates) {
			if (date == null) continue;
			if (latest == null || date.isAfter(latest)) latest = date;
		}
		return latest;
	}

	public static DateAndTime latest(DateAndTime... dates) {
		DateAndTime latest = null;
		for (DateAndTime date : dates) {
			if (date == null) continue;
			if (latest == null || date.isAfter(latest)) latest = date;
		}
		return latest;
	}

	public static DateAndTime fromString(String dateAndTimeString) {
		if (Str.isBlank(dateAndTimeString)) return null;
		return new DateAndTime(dateAndTimeString);
	}

}
