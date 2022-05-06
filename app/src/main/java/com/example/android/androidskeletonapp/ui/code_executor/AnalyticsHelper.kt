package com.example.android.androidskeletonapp.ui.code_executor

import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.DimensionalResponse
import org.hisp.dhis.android.core.arch.helpers.Result
import java.lang.Math.ceil

object AnalyticsHelper {

    private const val width = 8

    @JvmStatic
    @JvmSuppressWildcards
    fun prettyPrint(result: Result<DimensionalResponse, out @JvmSuppressWildcards AnalyticsException>): String {
        return when(result) {
            is Result.Success -> prettyPrintSuccess(result.value)
            is Result.Failure -> "There was an error: ${result.failure.message}"
        }
    }

    private fun prettyPrintSuccess(response: DimensionalResponse): String {
        val columnHeaderSize = ceil(response.dimensions.size / 2.0).toInt()

        val columnDimensions = response.dimensions.subList(0, columnHeaderSize)
        val rowDimensions = response.dimensions.subList(columnHeaderSize, response.dimensions.size)

        val columnHeaders = getHeaderCombination(columnDimensions, response.dimensionItems)
        val rowHeaders = getHeaderCombination(rowDimensions, response.dimensionItems)

        val sb = StringBuilder()

        // Column headers
        val initialEmptySpace = rowDimensions.size * width

        (0 until columnHeaderSize).map { headerIdx ->
            sb.append("".padStart(initialEmptySpace))
            columnHeaders.forEach { header ->
                val id = header[headerIdx]
                val name = response.metadata[id.id]!!.displayName
                sb.append(cell(name))
            }
            sb.append("\n")
        }

        // Rows
        rowHeaders.forEach { headerDim ->
            headerDim.forEach { dimItem ->
                val name = response.metadata[dimItem.id]!!.displayName
                sb.append(cell(name))
            }
            columnHeaders.forEach { columDim ->
                val valueDimensions = (headerDim + columDim).map { it.id }
                val value = response.values.find { value -> value.dimensions.containsAll(valueDimensions) }?.value ?: ""
                sb.append(cell(value))
            }
            sb.append("\n")
        }


        return sb.toString()
    }

    private fun getHeaderCombination(dimensions: List<Dimension>,
                                     dimensionItems: Map<Dimension, List<DimensionItem>>): List<List<DimensionItem>> {
        return dimensions.map { dimensionItems[it]!! }
            .fold(listOf(listOf())) { acc, dimension ->
                acc.flatMap { list -> dimension.map { element -> list + element } }
            }
    }

    private fun cell(value: String): String {
        val padSize = width - 1
        val paddedValue =
            if (value.length >= padSize) value.substring(0, padSize - 1)
            else value

        return "|${paddedValue.padEnd(padSize)}"
    }

}