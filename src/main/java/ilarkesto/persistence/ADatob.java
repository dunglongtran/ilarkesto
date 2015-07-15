/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
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
package ilarkesto.persistence;

import ilarkesto.base.OverrideExpectedException;
import ilarkesto.core.base.Str;
import ilarkesto.core.search.SearchText;
import ilarkesto.core.search.Searchable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Base class for classes with persistent data.
 */
public abstract class ADatob implements Datob, Searchable {

	protected abstract ADatobManager getManager();

	public void updateProperties(Map<String, String> properties) {}

	public final HashMap<String, String> createPropertiesMap() {
		HashMap<String, String> properties = new HashMap<String, String>();
		storeProperties(properties);
		return properties;
	}

	protected void storeProperties(Map<String, String> properties) {
		properties.put("@type", Str.getSimpleName(getClass()));
	}

	protected void updateLastModified() {
		ADatobManager manager = getManager();
		if (manager == null) return;
		manager.updateLastModified(this);
	}

	protected void fireModified(String field, String value) {
		ADatobManager manager = getManager();
		if (manager == null) return;
		manager.onDatobModified(this, field, value);
	}

	protected final void repairMissingMaster() {
		ADatobManager manager = getManager();
		if (manager == null) return;
		manager.onMissingMaster(this);
	}

	@Override
	public boolean matches(SearchText searchText) {
		return false;
	}

	protected void repairDeadReferences(String entityId) {}

	public void ensureIntegrity() {}

	public boolean isPersisted() {
		ADatobManager manager = getManager();
		if (manager == null) return false;
		return manager.isPersisted();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	// --- properties as map ---

	// --- helper ---

	protected static void repairDeadReferencesOfValueObjects(Collection<? extends ADatob> valueObjects, String entityId) {
		for (ADatob vo : valueObjects)
			vo.repairDeadReferences(entityId);
	}

	protected final <S extends AStructure> Set<S> cloneValueObjects(Collection<S> strucktures,
			StructureManager<S> manager) {
		Set<S> ret = new HashSet<S>();
		for (S s : strucktures) {
			ret.add((S) s.clone(manager));
		}
		return ret;
	}

	protected static Set<String> getIdsAsSet(Collection<? extends AEntity> entities) {
		Set<String> result = new HashSet<String>(entities.size());
		for (AEntity entity : entities)
			result.add(entity.getId());
		return result;
	}

	protected static List<String> getIdsAsList(Collection<? extends AEntity> entities) {
		List<String> result = new ArrayList<String>(entities.size());
		for (AEntity entity : entities)
			result.add(entity.getId());
		return result;
	}

	protected static boolean matchesKey(String s, String key) {
		if (s == null) return false;
		return s.toLowerCase().contains(key);
	}

	protected void repairDeadDatob(ADatob datob) {
		throw new OverrideExpectedException();
	}

	public class StructureManager<D extends ADatob> extends ADatobManager<D> {

		@Override
		public void onDatobModified(D datob, String field, String value) {
			fireModified(field, value);
		}

		@Override
		public void updateLastModified(D datob) {
			ADatob.this.updateLastModified();
		}

		@Override
		public void onMissingMaster(D datob) {
			repairDeadDatob(datob);
		}

		@Override
		public void ensureIntegrityOfStructures(Collection<D> structures) {
			for (ADatob structure : new ArrayList<ADatob>(structures)) {
				((AStructure) structure).setManager(this);
				structure.ensureIntegrity();
			}
		}

		@Override
		public boolean isPersisted() {
			return ADatob.this.isPersisted();
		}

	}

}
