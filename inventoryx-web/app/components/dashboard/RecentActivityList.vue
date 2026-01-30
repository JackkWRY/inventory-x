<script setup lang="ts">
import { useDateFormat } from "@vueuse/core";
import { useI18n } from "vue-i18n";
import type { DashboardActivity } from "~/types/dashboard";

defineProps<{
  activities: DashboardActivity[];
}>();

const { t } = useI18n();

const formatDate = (date: string) => {
  return useDateFormat(date, "DD/MM/YYYY HH:mm").value;
};

const parseQty = (qty: string | number) => {
  return typeof qty === 'string' ? parseFloat(qty) : qty;
};

const getMovementTypeClass = (type: string) => {
  switch (type) {
    case "RECEIPT": return "text-success";
    case "WITHDRAWAL":
    case "SALE":
      return "text-danger";
    case "RESERVATION": return "text-warning";
    default: return "text-info";
  }
};
</script>

<template>
  <div class="activity-list-container">
    <h3 class="section-title">{{ t('dashboard.recentActivity') }}</h3>
    
    <div v-if="activities.length === 0" class="empty-state">
      {{ t('messages.noData') }}
    </div>

    <table v-else class="activity-table">
      <thead>
        <tr>
          <th>{{ t('inventory.performedAt') }}</th>
          <th>{{ t('inventory.sku') }}</th>
          <th>{{ t('inventory.movementType') }}</th>
          <th class="text-right">{{ t('inventory.quantity') }}</th>
          <th>{{ t('inventory.location') }}</th>
          <th>{{ t('inventory.performedBy') }}</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="activity in activities" :key="activity.id">
          <td class="text-sm text-secondary">{{ formatDate(activity.performedAt) }}</td>
          <td class="font-mono font-medium">{{ activity.sku }}</td>
          <td>
            <span :class="getMovementTypeClass(activity.type)">
               {{ t(`movementTypes.${activity.type}`) }}
            </span>
          </td>
          <td class="text-right font-mono">
             <span :class="parseQty(activity.quantity) > 0 ? 'text-success' : 'text-danger'">
               {{ parseQty(activity.quantity) > 0 ? '+' : '' }}{{ activity.quantity }}
             </span>
          </td>
          <td class="text-sm">{{ activity.locationName || '-' }}</td>
          <td class="text-sm text-secondary">{{ activity.performedBy }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.activity-list-container {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  overflow: hidden;
}

.section-title {
  padding: 1rem 1.5rem;
  margin: 0;
  border-bottom: 1px solid var(--color-border);
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--color-text-primary);
}

.activity-table {
  width: 100%;
  border-collapse: collapse;
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.activity-table th {
  padding: 0.875rem 1.5rem;
  text-align: left;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--color-text-secondary);
  background: var(--glass-bg-strong);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border-bottom: 1px solid var(--color-border);
}

.activity-table td {
  padding: 0.875rem 1.5rem;
  border-bottom: 1px solid var(--color-border);
  color: var(--color-text-primary);
  font-size: 0.875rem;
  transition: all 0.2s ease;
}

.activity-table tbody tr {
  transition: all 0.2s ease;
}

.activity-table tbody tr:hover {
  background: var(--color-surface-hover);
}

.activity-table tbody tr:hover td:first-child {
  box-shadow: inset 3px 0 0 var(--color-primary);
}

.activity-table tr:last-child td {
  border-bottom: none;
}

.text-right { text-align: right; }
.font-mono { font-family: monospace; }
.font-medium { font-weight: 500; }
.text-sm { font-size: 0.875rem; }
.text-secondary { color: var(--color-text-secondary); }

.text-success { color: #10b981; }
.text-danger { color: #ef4444; }
.text-warning { color: #f59e0b; }
.text-info { color: #3b82f6; }

.empty-state {
  padding: 2rem;
  text-align: center;
  color: var(--color-text-secondary);
}
</style>
