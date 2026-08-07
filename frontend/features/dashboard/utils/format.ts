export function formatCurrency(value: number): string {
  return `₹${value.toLocaleString('en-IN')}`;
}

export function formatQuantity(value: number): string {
  return `${value.toLocaleString('en-IN')} KG`;
}

export function formatWeight(value: number): string {
  return `${value.toLocaleString('en-IN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })} KG`;
}
