package com.dnebinger.liferay.vaadin.sample;

import com.alsnightsoft.vaadin.containers.LazyPagedContainer;
import com.alsnightsoft.vaadin.containers.LazyQuery;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.vaadin.data.Property;
import com.vaadin.data.util.VaadinPropertyDescriptor;
import com.vaadin.ui.Button;
import org.apache.commons.lang.ClassUtils;

import java.util.*;

/**
 * class LazyUserContainer: A lazy container for Liferay users.
 * @author dnebinger
 */
public class LazyUserContainer extends LazyPagedContainer<User> {
	private static final Log logger = LogFactoryUtil.getLog(LazyUserContainer.class);
	private static final long serialVersionUID = 5856434647821524226L;
	private List<String> extraPropertyIds = new ArrayList<String>();
	private Map<String, Class<?>> extraTypes = new Hashtable<String, Class<?>>();

	@Override
	public boolean addContainerProperty(Object propertyId, final Class<?> type, Object defaultValue) throws UnsupportedOperationException {
		if (propertyId != null && type != null && ClassUtils.isAssignable(propertyId.getClass(), String.class)) {
			String pid = (String) propertyId;

			if(extraPropertyIds.contains(pid)) {
				return false;
			} else {
				extraPropertyIds.add(pid);
				extraTypes.put(pid, type);

				fireContainerPropertySetChange();

				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public Collection<String> getContainerPropertyIds() {
		if ((extraPropertyIds == null) || (extraPropertyIds.isEmpty()))
		return super.getContainerPropertyIds();

		List<String> ids = new LinkedList<String>();

		ids.addAll(super.getContainerPropertyIds());

		ids.addAll(extraPropertyIds);

		return ids;
	}

	@Override
	protected Collection<?> getSortablePropertyIds() {

			LinkedList sortables = new LinkedList();
			Iterator i$ = this.getContainerPropertyIds().iterator();

			while(i$.hasNext()) {
				Object propertyId = i$.next();
				Class propertyType = this.getType(propertyId);

				if ((propertyId == null) || (propertyType == null)) continue;

				if(Comparable.class.isAssignableFrom(propertyType) || propertyType.isPrimitive()) {
					sortables.add(propertyId);
				}
			}

			return sortables;
	}

	@Override
	public Class<?> getType(Object propertyId) {
		if (extraPropertyIds != null && ! extraPropertyIds.isEmpty()) {
			if (propertyId != null && ClassUtils.isAssignable(propertyId.getClass(), String.class)) {
				return extraTypes.get((String) propertyId);
			}
		}

		Class<?> type = super.getType(propertyId);

		if (type != null) return type;

		logger.warn("Could not find type for [" + propertyId + "].");

		return Object.class;
	}
	/**
	 * LazyUserContainer: Constructor.
	 */
	public LazyUserContainer() {
		super(User.class);

		setLazyQuery(new LazyQuery<User>() {
			@Override
			public int getLazySize() {
				int sz = 0;

				try {
					sz = UserLocalServiceUtil.getUsersCount();

					if (logger.isDebugEnabled()) logger.debug("Found " + sz + " users to display.");

				} catch (SystemException e) {
					logger.error("Error accessing users count: " + e.getMessage(), e);
				}

				return sz;
			}

			@Override
			public List<User> getLazyItemsIds(int start, int end) {
				if (logger.isDebugEnabled()) logger.debug("Returning users from " + start + " to " + end + ".");

				try {
					return UserLocalServiceUtil.getUsers(start, end);
				} catch (SystemException e) {
					logger.error("Error fetching users from " + start + " to " + end + ": " + e.getMessage(), e);
				}

				return null;
			}

			/**
			 * getLazyFilteredSize: Supposed to allow for returning a count where a filter is being applied, but we are not using filters so we're not worried about this.
			 * @return int Always returns zero.
			 */
			@Override
			public int getLazyFilteredSize() {
				return 0;
			}

			/**
			 * getLazyFilteredItemsIds: Supposed to return the filtered sublist, but we are not supporting filters.
			 * @param start
			 * @param end
			 * @return List Always returns null.
			 */
			@Override
			public List<User> getLazyFilteredItemsIds(int start, int end) {
				return null;
			}
		});

		setApplyLazyFilter(false);
	}
}
