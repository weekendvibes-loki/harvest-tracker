# Harvest Tracker Design System

**Location**: `docs/frontend/DESIGN_SYSTEM.md`  
**Version**: 1.0.0  
**Target Stack**: Next.js 14+, Tailwind CSS v3/v4, shadcn/ui, Lucide Icons  

---

> [!IMPORTANT]
> **MONKEY CODE PROMPT MANDATE**  
> Every Monkey Code prompt for frontend work **MUST** begin with:  
> `Read docs/frontend/DESIGN_SYSTEM.md and follow it strictly.`

---

## 1. Color Palette

The color system uses harmonious HSL tailwind colors tailored for modern agriculture software — clean greens, warm amber/mango accents, neutral slate surfaces, and semantic indicators.

### 1.1 Brand & Core Palette

| Role | Token Name | Light HSL / Value | Dark HSL / Value | Hex | Usage |
|---|---|---|---|---|---|
| **Primary** | `--primary` | `142 76% 36%` | `142 70% 45%` | `#16a34a` | Primary buttons, active tabs, brand anchors |
| **Primary Foreground** | `--primary-foreground` | `0 0% 100%` | `0 0% 100%` | `#ffffff` | Text on primary buttons |
| **Accent (Mango)** | `--accent-mango` | `38 92% 50%` | `38 92% 54%` | `#f59e0b` | Harvest metrics, highlight badges, warm indicators |
| **Secondary** | `--secondary` | `140 30% 96%` | `144 20% 18%` | `#f0fdf4` | Secondary buttons, subtle highlights |
| **Secondary Foreground**| `--secondary-foreground` | `145 65% 20%` | `140 40% 90%` | `#14532d` | Text on secondary elements |
| **Background** | `--background` | `210 20% 98%` | `222 47% 11%` | `#f8fafc` | Main application canvas |
| **Surface / Card** | `--card` | `0 0% 100%` | `222 47% 15%` | `#ffffff` | Cards, modals, table backgrounds |
| **Border** | `--border` | `214 32% 91%` | `217 19% 27%` | `#e2e8f0` | Dividers, card borders, input borders |
| **Muted** | `--muted` | `210 40% 96.1%` | `217.2 32.6% 17.5%` | `#f1f5f9` | Disabled states, table headers |
| **Muted Foreground** | `--muted-foreground` | `215.4 16.3% 46.9%` | `215 20.2% 65.1%` | `#64748b` | Secondary text, captions, metadata |

### 1.2 Semantic Status Palette

| Status | Background Light | Text Light | Border Light | Usage |
|---|---|---|---|---|
| **Success** | `#dcfce7` (`emerald-100`) | `#15803d` (`emerald-700`) | `#86efac` (`emerald-300`) | Paid invoices, confirmed harvests, present workers |
| **Warning** | `#fef3c7` (`amber-100`) | `#b45309` (`amber-700`) | `#fde68a` (`amber-300`) | Partial payments, pending approvals, low stock |
| **Error / Destructive** | `#fee2e2` (`red-100`) | `#b91c1c` (`red-700`) | `#fca5a5` (`red-300`) | Overdue invoices, cancelled orders, rejected records |
| **Info** | `#e0f2fe` (`sky-100`) | `#0369a1` (`sky-700`) | `#7dd3fc` (`sky-300`) | Draft orders, in-transit dispatches, general notifications |

---

## 2. Typography

The font hierarchy prioritizes legibility for high-density tabular data and field metrics.

- **Primary Font**: `Inter`, `-apple-system`, `BlinkMacSystemFont`, `sans-serif`
- **Monospace Font**: `JetBrains Mono`, `ui-monospace`, `monospace` (for IDs, codes, numeric data)

### 2.1 Scale & Utility Classes

| Level | Size (rem / px) | Line Height | Weight | Tailwind Class | Usage |
|---|---|---|---|---|---|
| **Display 1** | `2.25rem / 36px` | `1.2` | `700 (Bold)` | `text-4xl font-bold tracking-tight` | Dashboard main title |
| **Heading 1** | `1.875rem / 30px` | `1.25` | `700 (Bold)` | `text-3xl font-bold tracking-tight` | Page headers |
| **Heading 2** | `1.5rem / 24px` | `1.3` | `600 (Semibold)`| `text-2xl font-semibold` | Section headers |
| **Heading 3** | `1.25rem / 20px` | `1.4` | `600 (Semibold)`| `text-xl font-semibold` | Card headers, modal titles |
| **Body Large** | `1.0rem / 16px` | `1.5` | `400 / 500` | `text-base font-normal` | Main content paragraphs |
| **Body Small** | `0.875rem / 14px` | `1.4` | `400 / 500` | `text-sm font-medium` | Form labels, table cells |
| **Caption** | `0.75rem / 12px` | `1.4` | `400 / 500` | `text-xs text-muted-foreground` | Timestamps, metadata, micro-copy |
| **Numeric Data** | `0.875rem / 14px` | `1.0` | `600 (Semibold)`| `font-mono font-semibold tabular-nums` | Quantities, financial amounts |

---

## 3. Border Radius

Use soft, modern rounded corners throughout the UI.

- **Small (`rounded-sm`)**: `0.25rem (4px)` — Checkboxes, badge indicators, subtle tags
- **Default (`rounded-md`)**: `0.375rem (6px)` — Input fields, buttons, dropdown menus
- **Large (`rounded-lg`)**: `0.5rem (8px)` — Cards, modals, table containers
- **Extra Large (`rounded-xl`)**: `0.75rem (12px)` — KPI metric blocks, dashboard widgets
- **Full (`rounded-full`)**: `9999px` — Avatars, status pills, circular icon containers

---

## 4. Elevation & Shadows

Shadows build spatial depth without visual noise.

- **Flat / None (`shadow-none`)**: Inline form controls, table cells
- **Subtle (`shadow-sm`)**: `0 1px 2px 0 rgb(0 0 0 / 0.05)` — Cards, default buttons, input focus states
- **Medium (`shadow-md`)**: `0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)` — Hover states, dropdowns, floating panels
- **Large (`shadow-lg`)**: `0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1)` — Modals, slide-over panels, popovers

---

## 5. Spacing Scale

The UI adheres strictly to a **4px / 8px grid system**.

| Token | Value | Tailwind Class | Common Usage |
|---|---|---|---|
| `space-1` | `4px` | `p-1`, `gap-1` | Micro padding, icon-text gap |
| `space-2` | `8px` | `p-2`, `gap-2` | Button inner padding, badge gap |
| `space-3` | `12px` | `p-3`, `gap-3` | Compact form field spacing |
| `space-4` | `16px` | `p-4`, `gap-4` | Card padding, standard grid gap |
| `space-6` | `24px` | `p-6`, `gap-6` | Main container padding, section gaps |
| `space-8` | `32px` | `p-8`, `gap-8` | Page layout margins |

---

## 6. Animation & Motion

Animations must feel snappy, responsive, and purposeful. Avoid sluggish transitions.

- **Fast (`duration-150`)**: Hover color transitions, button active states (`transition-colors duration-150 ease-in-out`)
- **Normal (`duration-200`)**: Accordion expansion, dropdown popovers (`transition-all duration-200 ease-out`)
- **Slow (`duration-300`)**: Modal fade-in, drawer slide-overs (`transition-transform duration-300 ease-in-out`)
- **Easing Function**: `cubic-bezier(0.4, 0, 0.2, 1)` (standard ease-in-out)

---

## 7. Icon Style

Use **Lucide React** icons exclusively for iconography.

- **Stroke Width**: `1.75px` or `2px` (never thick 3px or hairline 1px)
- **Standard Sizes**:
  - Small / Inline: `w-4 h-4` (`16px`)
  - Medium / Buttons: `w-5 h-5` (`20px`)
  - Large / Feature headers: `w-6 h-6` (`24px`)
  - Empty states: `w-12 h-12` (`48px`)
- **Coloring**: Match surrounding text color or use explicit semantic tokens (`text-emerald-600`, `text-amber-500`, `text-slate-400`).

---

## 8. Table Design

Tables are the core data view for harvests, workers, orders, and expenses.

```tsx
<div className="overflow-x-auto rounded-lg border border-slate-200 bg-white shadow-sm">
  <table className="w-full text-left text-sm">
    <thead className="bg-slate-50 text-xs font-semibold text-slate-500 uppercase tracking-wider border-b border-slate-200">
      <tr>
        <th className="px-4 py-3">Harvest Batch</th>
        <th className="px-4 py-3">Farm</th>
        <th className="px-4 py-3 text-right">Quantity</th>
        <th className="px-4 py-3">Status</th>
      </tr>
    </thead>
    <tbody className="divide-y divide-slate-100">
      <tr className="hover:bg-slate-50/80 transition-colors">
        <td className="px-4 py-3 font-mono font-medium text-slate-900">HV-2026-001</td>
        <td className="px-4 py-3 text-slate-700">Devgad Mango Orchard</td>
        <td className="px-4 py-3 text-right font-mono font-semibold tabular-nums">450.00 KG</td>
        <td className="px-4 py-3">
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-100 text-emerald-800">
            Confirmed
          </span>
        </td>
      </tr>
    </tbody>
  </table>
</div>
```

- **Headers**: Sticky, light gray background (`bg-slate-50`), uppercase caption font (`text-xs font-semibold tracking-wider`), bottom border.
- **Rows**: Subtle hover state (`hover:bg-slate-50/80`), 1px divide line (`divide-slate-100`), padding `px-4 py-3`.
- **Numbers**: Right-aligned with `font-mono tabular-nums`.

---

## 9. Form Design

Forms must feature high contrast, clear focus rings, and explicit inline error messaging.

```tsx
<div className="space-y-1.5">
  <label className="block text-sm font-medium text-slate-700">
    Harvest Quantity (KG) <span className="text-red-500">*</span>
  </label>
  <input
    type="number"
    placeholder="0.00"
    className="w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 shadow-sm placeholder:text-slate-400 focus:border-emerald-500 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 disabled:cursor-not-allowed disabled:bg-slate-50"
  />
  <p className="text-xs text-slate-500">Enter total weight in kilograms.</p>
</div>
```

- **Focus Ring**: `focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500`
- **Error State**: Border turns `border-red-500`, focus ring turns `focus:ring-red-500/20`, helper text turns `text-red-600`.

---

## 10. Card Style

Cards display summary stats, KPI metrics, and operational details.

```tsx
<div className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm hover:shadow-md transition-shadow">
  <div className="flex items-center justify-between">
    <span className="text-sm font-medium text-slate-500">Total Harvest</span>
    <div className="rounded-lg bg-emerald-50 p-2 text-emerald-600">
      <Sprout className="w-5 h-5" />
    </div>
  </div>
  <div className="mt-4 flex items-baseline justify-between">
    <span className="text-3xl font-bold tracking-tight text-slate-900 font-mono">12,450 KG</span>
    <span className="text-xs font-semibold text-emerald-600 bg-emerald-50 px-2 py-1 rounded-md">
      +14% vs last season
    </span>
  </div>
</div>
```

---

## 11. Button Variants

| Variant | Tailwind Classes | Purpose |
|---|---|---|
| **Primary** | `bg-emerald-600 hover:bg-emerald-700 text-white font-medium shadow-sm transition-colors` | Main page CTA (e.g. "Save Harvest") |
| **Secondary** | `bg-slate-100 hover:bg-slate-200 text-slate-900 font-medium transition-colors` | Secondary actions (e.g. "Export CSV") |
| **Outline** | `border border-slate-300 bg-white hover:bg-slate-50 text-slate-700 font-medium shadow-sm` | Cancel actions, filters |
| **Ghost** | `hover:bg-slate-100 text-slate-600 hover:text-slate-900 font-medium` | Inline table actions, icon buttons |
| **Destructive** | `bg-red-600 hover:bg-red-700 text-white font-medium shadow-sm` | Delete, reject, cancel invoice |

---

## 12. Loading Skeleton Style

Use pulsing neutral blocks matching the container layout.

```tsx
<div className="animate-pulse space-y-4">
  <div className="h-4 bg-slate-200 rounded w-1/4"></div>
  <div className="h-10 bg-slate-200 rounded w-full"></div>
  <div className="h-20 bg-slate-200 rounded w-full"></div>
</div>
```

---

## 13. Empty State Style

Used when tables or lists have zero records.

```tsx
<div className="flex flex-col items-center justify-center rounded-lg border-2 border-dashed border-slate-200 p-12 text-center bg-slate-50/50">
  <div className="rounded-full bg-slate-100 p-4 text-slate-400 mb-4">
    <Inbox className="w-8 h-8" />
  </div>
  <h3 className="text-base font-semibold text-slate-900">No harvest records found</h3>
  <p className="mt-1 text-sm text-slate-500 max-w-sm">
    No harvests have been recorded for this farm and season yet.
  </p>
  <button className="mt-6 inline-flex items-center gap-2 rounded-md bg-emerald-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-emerald-700">
    <Plus className="w-4 h-4" /> Record First Harvest
  </button>
</div>
```

---

## 14. Toast Style

Toast notifications communicate operational outcomes.

```tsx
// Success Toast
<div className="flex items-center gap-3 rounded-lg border border-emerald-200 bg-emerald-50 p-4 text-emerald-900 shadow-lg">
  <CheckCircle2 className="w-5 h-5 text-emerald-600 flex-shrink-0" />
  <div className="text-sm font-medium">Harvest record HV-2026-001 saved successfully.</div>
</div>
```
