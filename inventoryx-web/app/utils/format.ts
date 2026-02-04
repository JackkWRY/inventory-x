/**
 * Formats a numeric string or number into a readable quantity string with commas and decimals.
 * @param value - The value to format (string or number)
 * @returns Formatted string (e.g., "1,000.00")
 */
export const formatQuantity = (value: string | number): string => {
  const num = typeof value === "string" ? parseFloat(value || "0") : value;
  return num.toLocaleString("en-US", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
};
