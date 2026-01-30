<script setup lang="ts">
import type { ReceiveStockCommand } from "~/types/inventory";
import { useLocationStore } from "~/stores/location";
import { useProductStore } from "~/stores/product";
import type { Product } from "~/types/product";
import BaseModal from '~/components/common/BaseModal.vue'

/**
 * ReceiveStockDialog Component
 *
 * Modal dialog for receiving stock into warehouse.
 * Includes form validation and submission handling.
 */

// i18n
const { t } = useI18n()

// Props
interface Props {
  open: boolean;
  loading?: boolean;
  error?: string | null;
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  error: null,
});

// Emits
const emit = defineEmits<{
  submit: [command: ReceiveStockCommand];
  close: [];
}>();

// Stores
const locationStore = useLocationStore();
const productStore = useProductStore();

// Form state
const form = reactive({
  sku: "",
  locationId: "",
  quantity: "",
  unitOfMeasure: "PIECE",
  reason: "",
  performedBy: "",
});

// Product Search State
const productSearch = reactive({
  query: "",
  results: [] as Product[],
  showResults: false,
  loading: false
});

// Watch SKU changes
watch(() => form.sku, (newVal) => {
    if (!newVal) {
        productSearch.query = "";
    }
});

// Debounce search
let searchTimeout: NodeJS.Timeout;
const handleSearchInput = async (event: Event) => {
    const query = (event.target as HTMLInputElement).value;
    productSearch.query = query;
    form.sku = query;
    
    if (searchTimeout) clearTimeout(searchTimeout);
    
    if (query.length < 2) {
        productSearch.results = [];
        productSearch.showResults = false;
        return;
    }

    productSearch.loading = true;
    productSearch.showResults = true;
    
    searchTimeout = setTimeout(async () => {
        try {
            productSearch.results = await productStore.searchProducts(query);
        } finally {
            productSearch.loading = false;
        }
    }, 300);
};

const selectProduct = (product: Product) => {
    form.sku = product.sku;
    productSearch.query = `${product.sku} - ${product.name}`;
    productSearch.showResults = false;
    
    if (product.unitOfMeasure) {
        const uom = product.unitOfMeasure.toUpperCase();
        if (unitOptions.some(o => o.value === uom)) {
            form.unitOfMeasure = uom;
        }
    }
};

// Unit options
const unitOptions = [
  { value: "PIECE", label: "Piece" },
  { value: "KILOGRAM", label: "Kilogram" },
  { value: "LITER", label: "Liter" },
  { value: "METER", label: "Meter" },
  { value: "BOX", label: "Box" },
  { value: "PACK", label: "Pack" },
];

// Validation
const isValid = computed(() => {
  return (
    form.sku.trim().length >= 3 &&
    form.locationId.trim().length > 0 &&
    parseFloat(form.quantity) > 0 &&
    form.unitOfMeasure.length > 0 &&
    form.performedBy.trim().length > 0
  );
});

// Handle form submission
const handleSubmit = () => {
  if (!isValid.value) return;

  const command: ReceiveStockCommand = {
    sku: form.sku.trim().toUpperCase(),
    locationId: form.locationId.trim(),
    quantity: form.quantity,
    unitOfMeasure: form.unitOfMeasure,
    reason: form.reason.trim(),
    performedBy: form.performedBy.trim(),
  };

  emit("submit", command);
};

// Handle close - also close search results
const handleClose = () => {
  if (productSearch.showResults) {
    productSearch.showResults = false;
  } else {
    emit("close");
  }
};

// Initial Data Fetch
onMounted(async () => {
    await locationStore.fetchActiveLocations();
    // Click outside to close search results
    document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});

const handleClickOutside = (e: Event) => {
    const target = e.target as HTMLElement;
    if (!target.closest('.search-container')) {
        productSearch.showResults = false;
    }
};

// Reset form when dialog opens
watch(
  () => props.open,
  (isOpen) => {
    if (isOpen) {
      form.sku = "";
      productSearch.query = "";
      productSearch.results = [];
      form.locationId = "";
      form.quantity = "";
      form.unitOfMeasure = "PIECE";
      form.reason = "";
      form.performedBy = "";
      locationStore.fetchActiveLocations(); 
    }
  }
);
</script>

<template>
  <BaseModal
    :open="open"
    :title="t('inventory.receiveStock')"
    size="md"
    :close-on-escape="!productSearch.showResults"
    @close="handleClose"
  >
    <!-- Error Message -->
    <template #error>
      <div v-if="error" class="dialog__error">{{ error }}</div>
    </template>

    <!-- Form Body -->
    <template #body>
      <form @submit.prevent="handleSubmit">
        <!-- SKU (Searchable) -->
        <div class="form-group search-container">
          <label for="sku" class="form-label">
            {{ t('inventory.sku') }} <span class="required">*</span>
          </label>
          <div class="relative">
            <input
              id="sku"
              type="text"
              class="form-input"
              :value="productSearch.query"
              @input="handleSearchInput"
              @focus="productSearch.showResults = true"
              placeholder="Search Product by SKU or Name..."
              :disabled="loading"
              required
              autocomplete="off"
            />
            <!-- Search Results Dropdown -->
            <div v-if="productSearch.showResults && (productSearch.results.length > 0 || productSearch.loading)" class="search-results">
              <div v-if="productSearch.loading" class="search-item loading">Loading...</div>
              <div 
                v-else 
                v-for="product in productSearch.results" 
                :key="product.id" 
                class="search-item"
                @click="selectProduct(product)"
              >
                <div class="font-medium">{{ product.sku }}</div>
                <div class="text-sm text-muted">{{ product.name }}</div>
              </div>
            </div>
          </div>
          <span class="form-hint" v-if="!isValid && form.sku.length > 0 && form.sku.length < 3">
            {{ t('inventory.skuHint') }}
          </span>
        </div>

        <!-- Location (Select) -->
        <div class="form-group">
          <label for="location" class="form-label">
            {{ t('inventory.location') }} <span class="required">*</span>
          </label>
          <select
            id="location"
            v-model="form.locationId"
            class="form-input"
            :disabled="loading"
            required
          >
            <option value="" disabled>Select Location</option>
            <option v-for="loc in locationStore.activeLocations" :key="loc.id" :value="loc.id">
              {{ loc.name }} ({{ loc.type }})
            </option>
          </select>
          <div v-if="locationStore.activeLocations.length === 0" class="text-sm text-warning mt-1">
            No active locations found. Please create one first.
          </div>
        </div>

        <!-- Quantity and Unit -->
        <div class="form-row">
          <div class="form-group">
            <label for="quantity" class="form-label">
              {{ t('inventory.quantity') }} <span class="required">*</span>
            </label>
            <input
              id="quantity"
              v-model="form.quantity"
              type="number"
              class="form-input"
              placeholder="0"
              min="0.01"
              step="0.01"
              :disabled="loading"
              required
            />
          </div>
          <div class="form-group">
            <label for="unit" class="form-label">
              {{ t('inventory.unit') }} <span class="required">*</span>
            </label>
            <select
              id="unit"
              v-model="form.unitOfMeasure"
              class="form-input"
              :disabled="loading"
              required
            >
              <option
                v-for="unit in unitOptions"
                :key="unit.value"
                :value="unit.value"
              >
                {{ unit.label }}
              </option>
            </select>
          </div>
        </div>

        <!-- Reason -->
        <div class="form-group">
          <label for="reason" class="form-label">{{ t('inventory.reason') }}</label>
          <textarea
            id="reason"
            v-model="form.reason"
            class="form-input form-textarea"
            :placeholder="t('inventory.reasonPlaceholder')"
            :disabled="loading"
            rows="2"
          ></textarea>
        </div>

        <!-- Performed By -->
        <div class="form-group">
          <label for="performedBy" class="form-label">
            {{ t('inventory.performedBy') }} <span class="required">*</span>
          </label>
          <input
            id="performedBy"
            v-model="form.performedBy"
            type="text"
            class="form-input"
            placeholder="Your name or ID"
            :disabled="loading"
            required
          />
        </div>
      </form>
    </template>

    <!-- Footer -->
    <template #footer>
      <button
        type="button"
        class="btn btn--secondary"
        :disabled="loading"
        @click="emit('close')"
      >
        {{ t('common.cancel') }}
      </button>
      <button
        type="submit"
        class="btn btn--primary"
        :disabled="!isValid || loading"
        @click="handleSubmit"
      >
        <span v-if="loading" class="spinner"></span>
        {{ loading ? t('inventory.saving') : t('inventory.receiveStock') }}
      </button>
    </template>
  </BaseModal>
</template>

<style scoped>
/* Component-specific styles only */
.relative {
  position: relative;
}

.search-results {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  box-shadow: var(--shadow-lg);
  max-height: 200px;
  overflow-y: auto;
  z-index: 10;
  margin-top: 4px;
}

.search-item {
  padding: 0.5rem 0.75rem;
  cursor: pointer;
  border-bottom: 1px solid var(--color-border);
}

.search-item:last-child {
  border-bottom: none;
}

.search-item:hover {
  background-color: var(--color-surface-hover);
}

.search-item.loading {
  color: var(--color-text-secondary);
  font-style: italic;
  cursor: default;
}

.text-sm { font-size: 0.75rem; }
.text-muted { color: var(--color-text-muted); }
.text-warning { color: var(--color-warning); }
.font-medium { font-weight: 500; }

.spinner {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
