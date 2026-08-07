'use client';

import { useId } from 'react';

export interface LineChartPoint {
  label: string;
  value: number;
}

interface LineChartProps {
  data: LineChartPoint[];
  formatValue?: (value: number) => string;
  color?: string;
  fillOpacity?: string;
  className?: string;
}

const VIEWBOX_WIDTH = 640;
const VIEWBOX_HEIGHT = 240;
const MARGIN = { top: 16, right: 12, bottom: 30, left: 48 };
const TICK_COUNT = 5;

export function LineChart({
  data,
  formatValue = (value) => value.toLocaleString(),
  color = 'hsl(var(--primary))',
  fillOpacity = 'hsl(var(--primary) / 0.12)',
  className,
}: LineChartProps) {
  const gradientId = useId();

  if (data.length === 0) return null;

  const plotWidth = VIEWBOX_WIDTH - MARGIN.left - MARGIN.right;
  const plotHeight = VIEWBOX_HEIGHT - MARGIN.top - MARGIN.bottom;

  const values = data.map((point) => point.value);
  const rawMin = Math.min(...values);
  const rawMax = Math.max(...values);
  const span = rawMax - rawMin || 1;
  const pad = span * 0.15;
  const min = Math.floor((rawMin - pad) / 10) * 10;
  const max = Math.ceil((rawMax + pad) / 10) * 10;

  const x = (index: number) =>
    MARGIN.left + (data.length === 1 ? plotWidth / 2 : (index / (data.length - 1)) * plotWidth);
  const y = (value: number) => MARGIN.top + plotHeight - ((value - min) / (max - min)) * plotHeight;

  const linePath = data.map((point, index) => `${index === 0 ? 'M' : 'L'} ${x(index)} ${y(point.value)}`).join(' ');
  const areaPath = `${linePath} L ${x(data.length - 1)} ${MARGIN.top + plotHeight} L ${x(0)} ${MARGIN.top + plotHeight} Z`;

  const ticks = Array.from({ length: TICK_COUNT }, (_, index) => min + ((max - min) / (TICK_COUNT - 1)) * index);

  return (
    <svg
      viewBox={`0 0 ${VIEWBOX_WIDTH} ${VIEWBOX_HEIGHT}`}
      className={className ?? 'h-auto w-full'}
      role="img"
      aria-label="Line chart"
    >
      <defs>
        <linearGradient id={gradientId} x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor={color} stopOpacity="0.25" />
          <stop offset="100%" stopColor={color} stopOpacity="0" />
        </linearGradient>
      </defs>

      {ticks.map((tick) => (
        <line
          key={tick}
          x1={MARGIN.left}
          y1={y(tick)}
          x2={VIEWBOX_WIDTH - MARGIN.right}
          y2={y(tick)}
          stroke="hsl(var(--border))"
          strokeDasharray="4 4"
          strokeWidth="1"
        />
      ))}

      {ticks.map((tick) => (
        <text
          key={tick}
          x={MARGIN.left - 8}
          y={y(tick) + 4}
          textAnchor="end"
          className="fill-muted-foreground"
          style={{ fontSize: 11 }}
        >
          {formatValue(tick)}
        </text>
      ))}

      {data.map((point, index) => (
        <text
          key={point.label}
          x={x(index)}
          y={VIEWBOX_HEIGHT - 8}
          textAnchor="middle"
          className="fill-muted-foreground"
          style={{ fontSize: 11 }}
        >
          {point.label}
        </text>
      ))}

      <path d={areaPath} fill={`url(#${gradientId})`} />
      <path d={linePath} fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />

      {data.map((point, index) => (
        <circle key={point.label} cx={x(index)} cy={y(point.value)} r="3.5" fill={color} stroke="hsl(var(--card))" strokeWidth="1.5">
          <title>{`${point.label}: ${formatValue(point.value)}`}</title>
        </circle>
      ))}
    </svg>
  );
}
