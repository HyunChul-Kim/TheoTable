package com.theo.theotable.demo.repository

import android.content.Context
import com.theo.theotable.demo.model.table.DemoDataSet
import com.theo.theotable.demo.model.table.DemoTableData
import com.theo.theotable.sample.createSimpleTableData
import com.theo.theotable.sample.loadLargeTableData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DemoTableDataRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    suspend fun load(dataSet: DemoDataSet): Result<DemoTableData> = withContext(Dispatchers.IO) {
        return@withContext try {
            val tableData = when(dataSet) {
                DemoDataSet.Simple -> createSimpleTableData()
                DemoDataSet.Large -> context.loadLargeTableData()
            }

            Result.success(tableData)
        } catch(ce: CancellationException) {
            throw ce
        } catch(t: Throwable) {
            Result.failure(t)
        }
    }
}