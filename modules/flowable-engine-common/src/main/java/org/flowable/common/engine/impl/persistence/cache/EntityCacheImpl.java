/* Licensed under the Apache License, Version 2.0 (the "License");
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
package org.flowable.common.engine.impl.persistence.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.common.engine.impl.persistence.entity.Entity;

/**
 * @author Joram Barrez
 */
public class EntityCacheImpl implements EntityCache {

    protected Map<Class<?>, Map<String, CachedEntity>> cachedObjects = new HashMap<>();

    @Override
    public CachedEntity put(Entity entity, boolean storeState) {
        Map<String, CachedEntity> classCache = cachedObjects.get(entity.getClass());
        if (classCache == null) {
            classCache = new HashMap<>();
            cachedObjects.put(entity.getClass(), classCache);
        }
        CachedEntity cachedObject = new CachedEntity(entity, storeState);
        classCache.put(entity.getId(), cachedObject);
        return cachedObject;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T findInCache(Class<T> entityClass, String id) {
        CachedEntity cachedObject = null;
        Map<String, CachedEntity> classCache = cachedObjects.get(entityClass);

        if (classCache == null) {
            classCache = findClassCacheByCheckingSubclasses(entityClass);
        }

        if (classCache != null) {
            cachedObject = classCache.get(id);
        }

        if (cachedObject != null) {
            return (T) cachedObject.getEntity();
        }

        return null;
    }

    protected Map<String, CachedEntity> findClassCacheByCheckingSubclasses(Class<?> entityClass) {
        for (Class<?> clazz : cachedObjects.keySet()) {
            if (entityClass.isAssignableFrom(clazz)) {
                return cachedObjects.get(clazz);
            }
        }
        return null;
    }

    @Override
    public void cacheRemove(Class<?> entityClass, String entityId) {
        Map<String, CachedEntity> classCache = cachedObjects.get(entityClass);
        if (classCache == null) {
            return;
        }
        classCache.remove(entityId);
    }

    @Override
    public <T> Collection<CachedEntity> findInCacheAsCachedObjects(Class<T> entityClass) {
        Map<String, CachedEntity> classCache = cachedObjects.get(entityClass);
        if (classCache != null) {
            return classCache.values();
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> findInCache(Class<T> entityClass) {
        Map<String, CachedEntity> classCache = cachedObjects.get(entityClass);

        if (classCache == null) {
            classCache = findClassCacheByCheckingSubclasses(entityClass);
        }

        if (classCache != null) {
            ArrayList<T> entities = new ArrayList<>(classCache.size());
            for (CachedEntity cachedObject : classCache.values()) {

                // Non-deleted entities go first in the returned list,
                // while deleted ones go at the end.
                // This way users of this method will first get the 'active' entities.
                if (!cachedObject.getEntity().isDeleted()) {
                    entities.add(0, (T) cachedObject.getEntity());
                } else {
                    entities.add((T) cachedObject.getEntity());
                }

            }
            return entities;
        }

        return Collections.emptyList();
    }

    @Override
    public Map<Class<?>, Map<String, CachedEntity>> getAllCachedEntities() {
        return cachedObjects;
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
