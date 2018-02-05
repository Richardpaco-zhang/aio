package rd.zhang.aio.kotlin.core.function

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.Sort
import rd.zhang.aio.kotlin.core.base.DataProvider

fun <T : RealmObject> T.save() {
    DataProvider.getRealmInstance().transaction {
        if (this.hasPrimaryKey(it)) {
            it.copyToRealmOrUpdate(this)
        } else {
            it.copyToRealm(this)
        }
    }
}

fun <T : RealmObject> Collection<T>.saveAll() {
    DataProvider.getRealmInstance().transaction { realm ->
        forEach {
            if (it.hasPrimaryKey(realm)) {
                realm.copyToRealmOrUpdate(it)
            } else {
                realm.copyToRealm(it)
            }
        }
    }
}

fun <T : RealmObject> T.query(query: (RealmQuery<T>) -> Unit): List<T> {
    DataProvider.getRealmInstance().use {
        return it.copyFromRealm(it.entity(this).withQuery(query).findAll())
    }
}

fun <T : RealmObject> T.queryAll(): List<T> {
    DataProvider.getRealmInstance().use {
        return it.copyFromRealm(it.entity(this).findAll())
    }
}

fun <T : RealmObject> T.queryFrist(): T {
    DataProvider.getRealmInstance().use {
        return it.copyFromRealm(it.entity(this).findFirst())
    }
}

fun <T : RealmObject> T.queryFrist(query: (RealmQuery<T>) -> Unit): T {
    DataProvider.getRealmInstance().use {
        return it.copyFromRealm(it.entity(this).withQuery(query).findFirst())
    }
}

fun <T : RealmObject> T.queryLast(): T {
    DataProvider.getRealmInstance().use {
        return it.copyFromRealm(it.entity(this).findAll().last())
    }
}

fun <T : RealmObject> T.queryLast(query: (RealmQuery<T>) -> Unit): T {
    DataProvider.getRealmInstance().use {
        return it.copyFromRealm(it.entity(this).withQuery(query).findAll().last())
    }
}

fun <T : RealmObject> T.querySortdAsc(fileName: String): List<T> {
    DataProvider.getRealmInstance().use {
        return it.copyFromRealm(it.entity(this).findAll().sort(fileName, Sort.ASCENDING))
    }
}

fun <T : RealmObject> T.querySortdAsc(fileName: String, query: (RealmQuery<T>) -> Unit): List<T> {
    DataProvider.getRealmInstance().use {
        return it.copyFromRealm(it.entity(this).findAll().sort(fileName, Sort.ASCENDING))
    }
}

fun <T : RealmObject> T.querySortdDesc(fileName: String): List<T> {
    DataProvider.getRealmInstance().use {
        return it.copyFromRealm(it.entity(this).findAll().sort(fileName, Sort.DESCENDING))
    }
}

fun <T : RealmObject> T.querySortdDesc(fileName: String, query: (RealmQuery<T>) -> Unit): List<T> {
    DataProvider.getRealmInstance().use {
        return it.copyFromRealm(it.entity(this).findAll().sort(fileName, Sort.DESCENDING))
    }
}

fun <T : RealmObject> T.delete(query: (RealmQuery<T>) -> Unit) {
    DataProvider.getRealmInstance().transaction {
        it.entity(this).withQuery(query).findAll().deleteAllFromRealm()
    }
}

fun <T : RealmObject> T.deleteAll() {
    DataProvider.getRealmInstance().transaction {
        it.entity(this).findAll().deleteAllFromRealm()
    }
}

inline fun <reified T : RealmObject> T.count(): Long = DataProvider.getRealmInstance().where(T::class.java).count()

private fun <T : RealmObject> Realm.entity(instance: T): RealmQuery<T> {
    return RealmQuery.createQuery(this, instance.javaClass)
}

private fun <T> T.withQuery(block: (T) -> Unit): T {
    block(this); return this
}

private fun Realm.transaction(action: (Realm) -> Unit) {
    use { executeTransaction { action(this) } }
}

private fun <T : RealmObject> T.hasPrimaryKey(realm: Realm): Boolean {
    if (realm.schema.get(this.javaClass.simpleName) == null) {
        throw IllegalArgumentException("no find apply realm-android plugin!")
    }
    return realm.schema.get(this.javaClass.simpleName).hasPrimaryKey()
}