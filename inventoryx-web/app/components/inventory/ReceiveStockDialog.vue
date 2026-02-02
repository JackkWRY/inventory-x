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
 * Uses BaseModal for consistent dialog behavior.
 */

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
      <CommonErrorBanner v-if="error" :message="error" />
    </template>

    <!-- Form Body -->
    <template #body>
      <form @submit.prevent="handleSubmit">
        <!-- SKU (Searchable) -->
        <div class="form-group search-container">
          <label for="sku" class="form-label">
            {{ t('inventory.sku') }} <span class="text-danger">*</span>
          </label>
          <div class="search-wrapper">
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
            <div v-if="productSearch.showResults && (productSearch.results.length > 0 || productSearch.loading)" class="search-dropdown">
              <div v-if="productSearch.loading" class="search-dropdown__item search-dropdown__item--loading">
                <span class="spinner spinner--sm"></span> Loading...
              </div>
              <div 
                v-else 
                v-for="product in productSearch.results" 
                :key="product.id" 
                class="search-dropdown__item"
                @click="selectProduct(product)"
              >
                <div class="font-medium">{{ product.sku }}</div>
                <div class="text-xs text-muted">{{ product.name }}</div>
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
            {{ t('inventory.location') }} <span class="text-danger">*</span>
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
          <div v-if="locationStore.activeLocations.length === 0" class="form-hint text-warning">
            No active locations found. Please create one first.
          </div>
        </div>

        <!-- Quantity and Unit -->
        <div class="form-row">
          <div class="form-group">
            <label for="quantity" class="form-label">
              {{ t('inventory.quantity') }} <span class="text-danger">*</span>
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
              {{ t('inventory.unit') }} <span class="text-danger">*</span>
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
            {{ t('inventory.performedBy') }} <span class="text-danger">*</span>
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
        <span v-if="loading" class="spinner spinner--sm"></span>
        {{ loading ? t('inventory.saving') : t('inventory.receiveStock') }}
      </button>
    </template>
  </BaseModal>
</template>

<style scoped>
/* Search dropdown - component-specific only */
.search-wrapper {
  position: relative;
}

.search-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-lg);
  max-height: 200px;
  overflow-y: auto;
  z-index: 10;
  margin-top: 4px;
}

.search-dropdown__item {
  padding: 0.625rem 0.75rem;
  cursor: pointer;
  border-bottom: 1px solid var(--color-border-light);
  transition: background 0.15s ease;
}

.search-dropdown__item:last-child {
  border-bottom: none;
}

.search-dropdown__item:hover {
  background: var(--color-surface-hover);
}

.search-dropdown__item--loading {
  color: var(--color-text-secondary);
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: default;
}
</style>
