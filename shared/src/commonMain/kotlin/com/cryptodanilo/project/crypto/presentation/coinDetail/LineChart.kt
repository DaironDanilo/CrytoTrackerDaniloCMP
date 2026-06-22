package com.cryptodanilo.project.crypto.presentation.coinDetail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider

@Composable
fun LineChart(
    dataPoints: List<DataPoint>,
    style: ChartStyle,
    visibleDataPointsIndices: IntRange,
    unit: String,
    modifier: Modifier = Modifier,
    selectedDataPoint: DataPoint? = null,
    onSelectedDataPoint: (DataPoint) -> Unit = {},
    onXLabelWidthChange: (Float) -> Unit = {},
    showHelperLines: Boolean = true,
) {
    val textStyle =
        LocalTextStyle.current.copy(
            fontSize = style.labelFontSize,
        )
    val visibleDataPoints =
        remember(dataPoints, visibleDataPointsIndices) {
            dataPoints.slice(visibleDataPointsIndices)
        }
    val maxYValue =
        remember(visibleDataPoints) {
            visibleDataPoints.maxOfOrNull { it.y } ?: 0f
        }
    val minYValue =
        remember(visibleDataPoints) {
            visibleDataPoints.minOfOrNull { it.y } ?: 0f
        }
    val measurer = rememberTextMeasurer()

    var xLabelWidth by remember {
        mutableFloatStateOf(0f)
    }
    LaunchedEffect(key1 = xLabelWidth) {
        onXLabelWidthChange(xLabelWidth)
    }
    // Actual on-screen distance between data points, stretched to always span the
    // full viewport width — independent of xLabelWidth, which only reflects the
    // minimum spacing needed to keep labels from overlapping.
    var pointSpacingPx by remember {
        mutableFloatStateOf(0f)
    }
    val selectedDataPointIndex =
        remember(selectedDataPoint) {
            dataPoints.indexOf(selectedDataPoint)
        }
    var drawPoints by remember {
        mutableStateOf(listOf<DataPoint>())
    }
    var isShowingDataPoints by remember { mutableStateOf(selectedDataPoint != null) }

    Canvas(
        modifier =
            modifier
                .fillMaxSize()
                .clipToBounds()
                .pointerInput(drawPoints, pointSpacingPx) {
                    detectHorizontalDragGestures { change, _ ->
                        val newSelectedDataPointIndex =
                            getSelectedDataPointIndex(
                                touchOffsetX = change.position.x,
                                triggerWidth = pointSpacingPx,
                                drawPoints = drawPoints,
                            )
                        isShowingDataPoints =
                            (newSelectedDataPointIndex + visibleDataPointsIndices.first) in
                            visibleDataPointsIndices
                        if (isShowingDataPoints) {
                            onSelectedDataPoint(dataPoints[newSelectedDataPointIndex])
                        }
                    }
                },
    ) {
        val minLabelSpacingYPx = style.minYLabelSpacing.toPx()
        val verticalPaddingPx = style.verticalPadding.toPx()
        val horizontalPaddingPx = style.horizontalPadding.toPx()
        val xAxisLabelSpacingPx = style.xAxisLabelSpacing.toPx()

        val xLabelTextLayoutResults =
            visibleDataPoints.map { dataPoint ->
                measurer.measure(
                    text = dataPoint.xLabel,
                    style = textStyle.copy(textAlign = TextAlign.Center),
                )
            }
        val maxXLabelWidth = xLabelTextLayoutResults.maxOfOrNull { it.size.width } ?: 0
        val maxXLabelHeight = xLabelTextLayoutResults.maxOfOrNull { it.size.height } ?: 0
        val maxXLabelLineCount = xLabelTextLayoutResults.maxOfOrNull { it.lineCount } ?: 0
        val xLabelLineHeight =
            if (maxXLabelLineCount > 0) maxXLabelHeight / maxXLabelLineCount else 0
        val viewPortHeightPx =
            size.height -
                (maxXLabelHeight + 2 * verticalPaddingPx + xLabelLineHeight + xAxisLabelSpacingPx)
        val labelViewPortHeightPx = viewPortHeightPx + xLabelLineHeight

        // Y Label calculations.
        // Target a row pitch as tall as a data column is wide, so grid cells stay
        // roughly square instead of always packing rows at the legibility minimum
        // (which is what made the grid flatten into wide rectangles on wide canvases).
        // This is an approximation of the final per-column pixel width — it ignores
        // the left-axis label gutter computed below — close enough to pick a row count.
        val approxColumnWidthPx = size.width / visibleDataPoints.size.coerceAtLeast(1)
        val targetRowPitchPx = maxOf(approxColumnWidthPx, xLabelLineHeight + minLabelSpacingYPx)
        val labelCountExcludingLastLabel =
            (labelViewPortHeightPx / targetRowPitchPx).toInt().coerceAtLeast(1)
        val valueIncrement = (maxYValue - minYValue) / labelCountExcludingLastLabel
        val yLabels =
            (0..labelCountExcludingLastLabel).map { index ->
                ValueLabel(
                    value = maxYValue - (valueIncrement * index),
                    unit = unit,
                )
            }
        val yLabelTextLayoutResults =
            yLabels.map { valueLabel ->
                measurer.measure(
                    text = valueLabel.formatted(),
                    style = textStyle,
                )
            }
        val maxYLabelWidth = yLabelTextLayoutResults.maxOfOrNull { it.size.width } ?: 0

        val viewPortTopY = verticalPaddingPx + xLabelLineHeight + 10f
        // Reserve space on the right for half the last label's width (plus a small
        // buffer) so its centered position never needs to be edge-clamped into the
        // previous label's box — clamping without this room is what caused the last
        // x-axis label to overlap its neighbor.
        val rightPaddingPx = maxXLabelWidth / 2f + xAxisLabelSpacingPx
        val viewPortRightX = size.width - rightPaddingPx
        val viewPortBottomY = viewPortTopY + viewPortHeightPx
        val viewPortLeftX = 2f * horizontalPaddingPx + maxYLabelWidth
//        val viewPort = Rect(
//            left = viewPortLeftX,
//            top = viewPortTopY,
//            right = viewPortRightX,
//            bottom = viewPortBottomY
//        )
//        drawRect(
//            color = Color.Green.copy(alpha = 0.3f),
//            topLeft = viewPort.topLeft,
//            size = viewPort.size
//        )
        xLabelWidth = maxXLabelWidth + xAxisLabelSpacingPx
        // Stretch the visible data points to always span the full viewport width,
        // so the chart never leaves empty grid space after the last data point
        // when there are fewer points than the viewport could otherwise fit.
        pointSpacingPx =
            if (visibleDataPoints.size > 1) {
                (viewPortRightX - viewPortLeftX) / (visibleDataPoints.size - 1)
            } else {
                viewPortRightX - viewPortLeftX
            }
        // Cap how many x-axis labels are drawn so they stay evenly spaced and
        // readable regardless of how many data points are plotted.
        val maxXAxisLabels = 3
        val xAxisLabelStep = (visibleDataPoints.size / maxXAxisLabels).coerceAtLeast(1)
        xLabelTextLayoutResults.forEachIndexed { index, textLayoutResult ->
            val x = viewPortLeftX + pointSpacingPx * index
            if (index % xAxisLabelStep == 0 || index == visibleDataPoints.lastIndex) {
                val textX =
                    (x - textLayoutResult.size.width / 2f)
                        .coerceIn(0f, size.width - textLayoutResult.size.width)
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft =
                        Offset(
                            x = textX,
                            y = viewPortBottomY + xAxisLabelSpacingPx,
                        ),
                    // X-axis labels never change color on selection — only the
                    // crosshair and tooltip below indicate the selected point.
                    color = style.unselectedColor,
                )
            }
            // Only draw a vertical line for the selected (tapped/dragged) point — drawing
            // one per data point produced a dense barcode effect on long timeframes with
            // hundreds of points. Horizontal grid lines below are unaffected.
            if (showHelperLines && index == selectedDataPointIndex) {
                drawLine(
                    color = Color.White.copy(alpha = 0.6f),
                    start =
                        Offset(
                            x = x,
                            y = viewPortBottomY,
                        ),
                    end =
                        Offset(
                            x = x,
                            y = viewPortTopY,
                        ),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f),
                )
            }
            if (selectedDataPointIndex == index) {
                val valueLabel =
                    ValueLabel(
                        value = visibleDataPoints[index].y,
                        unit = unit,
                    )
                // Combined price + date tooltip at the top of the chart, Google
                // Finance style — single line, clamped so it never overflows the edges.
                val tooltipText = "${valueLabel.formatted()}  ${visibleDataPoints[index].xLabel}"
                val tooltipResult =
                    measurer.measure(
                        text = tooltipText,
                        style = textStyle.copy(color = Color.White),
                        maxLines = 1,
                    )
                val tooltipCenterX =
                    x.coerceIn(
                        tooltipResult.size.width / 2f,
                        size.width - tooltipResult.size.width / 2f,
                    )
                drawText(
                    textLayoutResult = tooltipResult,
                    topLeft =
                        Offset(
                            x = tooltipCenterX - tooltipResult.size.width / 2f,
                            y = viewPortTopY - tooltipResult.size.height - 10f,
                        ),
                    color = Color.White,
                )
            }
        }

        val heightRequiredForYLabels = xLabelLineHeight * (labelCountExcludingLastLabel + 1)
        val remainingHeightForYLabels = labelViewPortHeightPx - heightRequiredForYLabels
        val spaceBetweenLabels = remainingHeightForYLabels / labelCountExcludingLastLabel

        yLabelTextLayoutResults.forEachIndexed { index, textLayoutResult ->
            val x = horizontalPaddingPx + maxYLabelWidth - textLayoutResult.size.width.toFloat()
            val y =
                viewPortTopY + index * (xLabelLineHeight + spaceBetweenLabels) -
                    xLabelLineHeight / 2f
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft =
                    Offset(
                        x = x,
                        y = y,
                    ),
                color = style.unselectedColor,
            )
            if (showHelperLines) {
                // Thin and low-opacity on purpose — these are a subtle price reference,
                // not meant to compete visually with the chart line.
                drawLine(
                    color = style.unselectedColor.copy(alpha = 0.15f),
                    start =
                        Offset(
                            x = viewPortLeftX,
                            y = y + textLayoutResult.size.height.toFloat() / 2f,
                        ),
                    end =
                        Offset(
                            x = viewPortRightX,
                            y = y + textLayoutResult.size.height.toFloat() / 2f,
                        ),
                    strokeWidth = 0.5f,
                )
            }
        }

        // visibleDataPointsIndices = 5..19
        drawPoints =
            visibleDataPointsIndices.map {
                val x =
                    viewPortLeftX + (it - visibleDataPointsIndices.first) * pointSpacingPx

                // [minYValue, maxYValue] -> [0, 1]
                val ratio = (dataPoints[it].y - minYValue) / (maxYValue - minYValue)
                val y = viewPortBottomY - ratio * viewPortHeightPx
                DataPoint(
                    x = x,
                    y = y,
                    xLabel = dataPoints[it].xLabel,
                )
            }

        val conPoints1 = mutableListOf<DataPoint>()
        val conPoints2 = mutableListOf<DataPoint>()
        for (i in 1 until drawPoints.size) {
            val p0 = drawPoints[i - 1]
            val p1 = drawPoints[i]
            val x = (p1.x + p0.x) / 2
            val y1 = p0.y
            val y2 = p1.y
            conPoints1.add(DataPoint(x, y1, ""))
            conPoints2.add(DataPoint(x, y2, ""))
        }
        val linePath =
            Path().apply {
                if (drawPoints.isNotEmpty()) {
                    moveTo(drawPoints.first().x, drawPoints.first().y)
                    for (i in 1 until drawPoints.size) {
                        cubicTo(
                            x1 = conPoints1[i - 1].x,
                            y1 = conPoints1[i - 1].y,
                            x2 = conPoints2[i - 1].x,
                            y2 = conPoints2[i - 1].y,
                            x3 = drawPoints[i].x,
                            y3 = drawPoints[i].y,
                        )
                    }
                }
            }
        drawPath(
            path = linePath,
            color = style.charLineColor,
            style = Stroke(width = 5f, cap = StrokeCap.Round),
        )

        drawPoints.forEachIndexed { index, point ->
            // Only the selected/dragged point gets a dot — drawing one per data point
            // produced a "dotted line" effect on dense timeframes with hundreds of points.
            if (isShowingDataPoints && selectedDataPointIndex == index) {
                val circleOffset =
                    Offset(
                        x = point.x,
                        y = point.y,
                    )
                drawCircle(
                    color = style.selectedColor,
                    radius = 10f,
                    center = circleOffset,
                )
                drawCircle(
                    color = Color.White,
                    radius = 15f,
                    center = circleOffset,
                )
                drawCircle(
                    color = style.selectedColor,
                    radius = 15f,
                    center = circleOffset,
                    style = Stroke(width = 2f),
                )
            }
        }
        val filledAreaPath =
            Path().apply {
                if (drawPoints.isNotEmpty()) {
                    // Move to the starting point on the x-axis (below the first point)
                    moveTo(drawPoints.first().x, viewPortBottomY)

                    // Move up to the first point on the curve
                    lineTo(drawPoints.first().x, drawPoints.first().y)

                    // Create the cubic Bézier curve
                    for (i in 1 until drawPoints.size) {
                        cubicTo(
                            x1 = conPoints1[i - 1].x,
                            y1 = conPoints1[i - 1].y,
                            x2 = conPoints2[i - 1].x,
                            y2 = conPoints2[i - 1].y,
                            x3 = drawPoints[i].x,
                            y3 = drawPoints[i].y,
                        )
                    }

                    // Close the path back to the x-axis at the last point
                    lineTo(drawPoints.last().x, viewPortBottomY)
                    close()
                }
            }

        // Draw the filled area under the curve — fades from semi-transparent at the
        // top of the plotted range to fully transparent at the x-axis, instead of a
        // flat tint, for the "glow under the line" look.
        drawPath(
            path = filledAreaPath,
            brush =
                Brush.verticalGradient(
                    colors = listOf(style.charLineColor.copy(alpha = 0.4f), Color.Transparent),
                    startY = viewPortTopY,
                    endY = viewPortBottomY,
                ),
            style = androidx.compose.ui.graphics.drawscope.Fill,
        )
    }
}

private fun getSelectedDataPointIndex(
    touchOffsetX: Float,
    triggerWidth: Float,
    drawPoints: List<DataPoint>,
): Int {
    val triggerRangeLeft = touchOffsetX - triggerWidth / 2
    val triggerRangeRight = touchOffsetX + triggerWidth / 2
    return drawPoints.indexOfFirst { dataPoint ->
        dataPoint.x in triggerRangeLeft..triggerRangeRight
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 300, name = "Line Chart")
@Composable
private fun LineChartPreview() {
    CryptoTrackerThemeProvider(darkTheme = false) {
        val previewDataPoints =
            remember {
                (1..20).map { i ->
                    DataPoint(
                        x = i.toFloat(),
                        y = 30000f + i * 1000f + (i % 5) * 500f,
                        xLabel = "${i % 12 + 1}h\n5/$i",
                    )
                }
            }
        LineChart(
            dataPoints = previewDataPoints,
            style =
                ChartStyle(
                    charLineColor = Color(0xFF6650A4),
                    unselectedColor = Color(0xFF7C7C7C),
                    selectedColor = Color(0xFF6650A4),
                    helperLinesThicknessPx = 5f,
                    axisLinesThicknessPx = 5f,
                    labelFontSize = 14.sp,
                    minYLabelSpacing = CryptoTrackerTheme.sizing.chartMinYLabelSpacing,
                    verticalPadding = CryptoTrackerTheme.spacing.small,
                    horizontalPadding = CryptoTrackerTheme.spacing.small,
                    xAxisLabelSpacing = CryptoTrackerTheme.spacing.small,
                ),
            visibleDataPointsIndices = 0..previewDataPoints.lastIndex,
            unit = "$",
            selectedDataPoint = previewDataPoints[2],
        )
    }
}
