/**
 * Dashboard Types
 */

export interface DashboardActivity {
  id: string;
  type: string;
  quantity: number | string;
  sku: string;
  locationName: string;
  performedBy: string;
  performedAt: string;
}

export interface DashboardData {
  totalStockValue: number;
  totalItems: number;
  lowStockItems: number;
  totalLocations: number;
  recentActivities: DashboardActivity[];
}
