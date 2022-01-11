package com.smallcake.temp.utils

import com.smallcake.temp.MyApplication
import com.smallcake.temp.bean.ConstactBean
import com.smallcake.temp.bean.ConstactBeanDao
import org.greenrobot.greendao.query.QueryBuilder


object GreenDaoHelper {

     private val dao = MyApplication.daoSession

    private fun constactBeanQuery(): QueryBuilder<ConstactBean>? {
        return try {
            dao.constactBeanDao.queryBuilder()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 增
     */
    fun insert(bean: ConstactBean): Long {
        return dao.insert(bean)
    }
    /**
     * 删
     */
    fun del(wherecluse: String) {
        val tj = ConstactBeanDao.Properties.Name.eq(wherecluse)
        constactBeanQuery()?.apply {
            where(tj).buildDelete().executeDeleteWithoutDetachingEntities()
        }
    }
    /**
     * 删全部
     */
    fun delAll() {
        try {
            dao.constactBeanDao.deleteAll()
        } catch (e: Exception) {
        }
    }

    /**
     * 改
     */
    fun update(bean:ConstactBean?,newName:String) {
        if (bean==null)return
        val tj = ConstactBeanDao.Properties.Id.eq(bean.id)
        constactBeanQuery()?.apply {
            val oldBean = where(tj).build().unique()
            oldBean?.apply {
                name = newName
                dao.constactBeanDao.update(oldBean)
            }
        }

    }

    /**
     * 按条件查询数据
     * 精确查询
     */
    fun searchByWhere(wherecluse: String): List<ConstactBean>? {
        val tj = ConstactBeanDao.Properties.Name.eq(wherecluse)
        return constactBeanQuery()?.where(tj)?.build()?.list()

    }
    /**
     * 按条件查询数据
     * 模糊查询
     */
    fun searchLike(wherecluse: String): List<ConstactBean>? {
        val tj = ConstactBeanDao.Properties.Name.like("%$wherecluse%")
        return  constactBeanQuery()?.where(tj)?.build()?.list()
    }

    /**
     * 查所有
     */
    fun searchAll(): List<ConstactBean>? {
       return constactBeanQuery()?.list()
    }
}