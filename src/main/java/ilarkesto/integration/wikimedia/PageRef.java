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
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.integration.wikimedia;

import ilarkesto.json.AJsonWrapper;
import ilarkesto.json.JsonObject;

public class PageRef extends AJsonWrapper {

	public PageRef(long id, String title) {
		putMandatory("id", id);
		json.put("title", title);
	}

	public PageRef(JsonObject json) {
		super(json);
	}

	public Long getId() {
		return json.getLong("id");
	}

	public String getTitle() {
		return json.getString("title");
	}

	@Override
	public String toString() {
		return getId() + ": " + getTitle();
	}

}
