<script setup lang="ts">
import type { ReceiveStockCommand } from "~/types/inventory";
import { useLocationStore } from "~/stores/location";
import { useProductStore } from "~/stores/product";
import type { Product } from "~/types/product";

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

// Watch SKU changes to update search query if needed, or handle selection clearing
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
    form.sku = query; // Allow manual entry
    
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
    
    // Auto-select unit if available
    if (product.unitOfMeasure) {
        // Simple mapping or direct assignment if formats match
        // Assuming product UOM matches enum for now or defaulting
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
    sku: form.sku.trim().toUpperCase(), // Ensure only SKU is sent, even if query has name
    locationId: form.locationId.trim(),
    quantity: form.quantity,
    unitOfMeasure: form.unitOfMeasure,
    reason: form.reason.trim(),
    performedBy: form.performedBy.trim(),
  };

  emit("submit", command);
};

// Initial Data Fetch
onMounted(async () => {
    await locationStore.fetchActiveLocations();
    document.addEventListener("keydown", handleKeydown);
    // Click outside to close search results
    document.addEventListener('click', (e) => {
        const target = e.target as HTMLElement;
        if (!target.closest('.search-container')) {
            productSearch.showResults = false;
        }
    });
});

onUnmounted(() => {
  document.removeEventListener("keydown", handleKeydown);
});

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
      // Refresh locations to be safe
      locationStore.fetchActiveLocations(); 
    }
  }
);

// Handle backdrop click
const handleBackdropClick = (event: MouseEvent) => {
  if ((event.target as HTMLElement).classList.contains("dialog-backdrop")) {
    emit("close");
  }
};

// Handle escape key
const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === "Escape" && props.open) {
    if (productSearch.showResults) {
        productSearch.showResults = false;
    } else {
        emit("close");
    }
  }
};
</script>

<template>
  <Teleport to="body">
    <Transition name="fade">
      <div v-if="open" class="dialog-backdrop" @click="handleBackdropClick">
        <div
          class="dialog"
          role="dialog"
          aria-modal="true"
          aria-labelledby="dialog-title"
        >
          <!-- Header -->
          <div class="dialog__header">
            <h2 id="dialog-title" class="dialog__title">{{ t('inventory.receiveStock') }}</h2>
            <button
              class="dialog__close"
              @click="emit('close')"
              aria-label="Close dialog"
            >
              ✕
            </button>
          </div>

          <!-- Error Message -->
          <div v-if="error" class="dialog__error">
            {{ error }}
          </div>

          <!-- Form -->
          <form class="dialog__body" @submit.prevent="handleSubmit">
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

          <!-- Footer -->
          <div class="dialog__footer">
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
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.dialog-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.dialog {
  background: var(--glass-bg-strong);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl);
  max-width: 480px;
  width: 100%;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.dialog__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid var(--color-border);
}

.dialog__title {
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0;
  color: var(--color-text-primary);
}

.dialog__close {
  background: none;
  border: none;
  font-size: 1.25rem;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: 0.25rem;
  line-height: 1;
  border-radius: 4px;
  transition: background 0.2s;
}

.dialog__close:hover {
  background: var(--color-surface-hover);
}

.dialog__error {
  margin: 1rem 1.5rem 0;
  padding: 0.75rem 1rem;
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.2);
  border-radius: 4px;
  color: #dc2626;
  font-size: 0.875rem;
}

.dialog__body {
  padding: 1.5rem;
  overflow-y: auto;
}

.dialog__footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid var(--color-border);
  background: transparent; /* Changed from surface to match glass */
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-text-primary);
  margin-bottom: 0.375rem;
}

.required {
  color: #dc2626;
}

.form-hint {
  display: block;
  margin-top: 0.25rem;
  font-size: 0.75rem;
  color: var(--color-text-secondary);
}

/* Spinner */
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
  to {
    transform: rotate(360deg);
  }
}

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.fade-enter-active .dialog,
.fade-leave-active .dialog {
  transition: transform 0.2s ease;
}

.fade-enter-from .dialog,
.fade-leave-to .dialog {
  transform: scale(0.95);
}

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

.text-sm {
  font-size: 0.75rem;
}

.text-muted {
  color: var(--color-text-muted);
}

.text-warning {
  color: var(--color-warning);
}

.font-medium {
  font-weight: 500;
}
</style>
