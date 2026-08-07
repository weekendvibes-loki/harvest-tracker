'use client';

export interface BarChartSeries {
  key: string;
  label: string;
  color: string;
}

export interface BarChartDatum {
  label: string;
  values: Record<string, number>;
}

interface BarChartProps {
  data: BarChartDatum[];
  series: BarChartSeries[];
  formatValue?: (value: number) => string;
  className?: string;
}

const VIEWBOX_WIDTH = 640;
const VIEWBOX_HEIGHT = 240;
const MARGIN = { top: 16, right: 12, bottom: 30, left: 48 };
const TICK_COUNT = 5;

export function BarChart({
  data,
  series,
  formatValue = (value) => value.toLocaleString(),
  className,
}: BarChartProps) {
  if (data.length === 0 || series.length === 0) return null;

  const plotWidth = VIEWBOX_WIDTH - MARGIN.left - MARGIN.right;
  const plotHeight = VIEWBOX_HEIGHT - MARGIN.top - MARGIN.bottom;

  const allValues = data.flatMap((datum) => series.map((s) => datum.values[s.key] ?? 0));
  const rawMax = Math.max(...allValues) || 1;
  const max = Math.ceil((rawMax * 1.1) / 10) * 10;

  const y = (value: number) => MARGIN.top + plotHeight - (value / max) * plotHeight;

  const groupWidth = plotWidth / data.length;
  const barWidth = (groupWidth * 0.6) / series.length;

  const ticks = Array.from({ length: TICK_COUNT }, (_, index) => (max / (TICK_COUNT - 1)) * index);

  return (
    <svg
      viewBox={`0 0 ${VIEWBOX_WIDTH} ${VIEWBOX_HEIGHT}`}
      className={className ?? 'h-auto w-full'}
      role="img"
      aria-label="Bar chart"
    >
      {ticks.map((tick) => (
        <g key={tick}>
          <line
            x1={MARGIN.left}
            y1={y(tick)}
            x2={VIEWBOX_WIDTH - MARGIN.right}
            y2={y(tick)}
            stroke="hsl(var(--border))"
            strokeDasharray="4 4"
            strokeWidth="1"
          />
          <text
            x={MARGIN.left - 8}
            y={y(tick) + 4}
            textAnchor="end"
            className="fill-muted-foreground"
            style={{ fontSize: 11 }}
          >
            {formatValue(tick)}
          </text>
        </g>
      ))}

      {data.map((datum, groupIndex) => {
        const groupStart = MARGIN.left + groupIndex * groupWidth + (groupWidth - barWidth * series.length) / 2;
        return (
          <g key={datum.label}>
            {series.map((s, seriesIndex) => {
              const value = datum.values[s.key] ?? 0;
              const barX = groupStart + seriesIndex * barWidth;
              const barHeight = (value / max) * plotHeight;
              return (
                <rect
                  key={s.key}
                  x={barX}
                  y={y(value)}
                  width={barWidth}
                  height={Math.max(barHeight, 1)}
                  rx="3"
                  fill={s.color}
                >
                  <title>{`${datum.label} ${s.label}: ${formatValue(value)}`}</title>
                </rect>
              );
            })}
            <text
              x={groupStart + (barWidth * series.length) / 2}
              y={VIEWBOX_HEIGHT - 8}
              textAnchor="middle"
              className="fill-muted-foreground"
              style={{ fontSize: 11 }}
            >
              {datum.label}
            </text>
          </g>
        );
      })}
    </svg>
  );
}
