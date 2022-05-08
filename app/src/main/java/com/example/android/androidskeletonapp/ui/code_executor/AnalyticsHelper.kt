package com.example.android.androidskeletonapp.ui.code_executor

import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionalResponse
import org.hisp.dhis.android.core.arch.helpers.Result
import kotlin.math.ceil

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

        val columnHeaders = getHeaderCombination(columnDimensions, response)
        val rowHeaders = getHeaderCombination(rowDimensions, response)

        val sb = StringBuilder()

        // Column headers
        val initialEmptySpace = rowDimensions.size * width

        (0 until columnHeaderSize).map { headerIdx ->
            sb.append("".padStart(initialEmptySpace))
            val headersByRow = columnHeaders.map { it[headerIdx] }
            countConsecutiveDuplicates(headersByRow).forEach { entry ->
                val id = entry.value
                val name = response.metadata[id]!!.displayName
                sb.append(cell(name, entry.count))
            }
            sb.append("\n")
        }

        // Rows
        rowHeaders.forEach { headerDim ->
            headerDim.forEach { id ->
                val name = response.metadata[id]!!.displayName
                sb.append(cell(name))
            }
            columnHeaders.forEach { columDim ->
                val valueDimensions = (headerDim + columDim).map { it }
                val value = response.values.find { value -> value.dimensions.containsAll(valueDimensions) }?.value ?: ""
                sb.append(cell(value))
            }
            sb.append("\n")
        }


        return sb.toString()
    }

    private fun getHeaderCombination(dimensions: List<Dimension>,
                                     response: DimensionalResponse): List<List<String>> {
        val dimensionItems = dimensions.associateWith { dimension ->
            val dimIndex = response.dimensions.indexOf(dimension)
            response.values.map { it.dimensions[dimIndex] }.toSet()
        }

        return dimensions.map { dimensionItems[it]!! }
            .fold(listOf(listOf())) { acc, dimension ->
                acc.flatMap { list -> dimension.map { element -> list + element } }
            }
    }

    private fun cell(value: String, size: Int = 1): String {
        val cellWidth = width * size
        val padSize = cellWidth - 1
        val paddedValue =
            if (value.length >= padSize) value.substring(0, padSize - 1)
            else value

        return "|${paddedValue.padEnd(padSize)}"
    }

    private fun countConsecutiveDuplicates(values: List<String>): List<Group> {
        val groups = mutableListOf<Group>()
        values.forEach {
            val last = groups.lastOrNull()
            if (last?.value == it) {
                last.count++
            } else {
                groups.add(Group(it, 1))
            }
        }
        return groups
    }

    data class Group(val value: String, var count: Int)
}