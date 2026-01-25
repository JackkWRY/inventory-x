<script setup lang="ts">
import type { CreateProductCommand, Product } from "~/types/product";
import { useForm } from "vee-validate";
import * as yup from "yup";

const props = defineProps<{
  initialData?: Product | null;
  loading: boolean;
  isEditMode: boolean;
  isOpen: boolean;
}>();

const emit = defineEmits(["submit", "cancel"]);
const { t } = useI18n();

// Validation Schema
const schema = computed(() => {
  return yup.object({
    sku: yup
      .string()
      .required(t("validation.required"))
      .min(3, t("validation.minLength", { min: 3 })),
    name: yup.string().required(t("validation.required")),
    description: yup.string().nullable(),
    category: yup.string().nullable(),
    price: yup
      .number()
      .typeError(t("validation.positiveNumber")) // Generic error for number types
      .positive(t("validation.positiveNumber"))
      .required(t("validation.required")),
    unitOfMeasure: yup.string().required(t("validation.required")),
  });
});

// Form Logic
const { values, errors, defineField, handleSubmit, resetForm, setValues } =
  useForm<CreateProductCommand>({
    validationSchema: schema,
    initialValues: {
      sku: "",
      name: "",
      description: "",
      category: "",
      price: 0,
      currency: "USD",
      unitOfMeasure: "PIECE",
    },
  });

const [sku] = defineField("sku");
const [name] = defineField("name");
const [description] = defineField("description");
const [category] = defineField("category");
const [price] = defineField("price");
const [unitOfMeasure] = defineField("unitOfMeasure");

// Watch initialData for Edit Mode
watch(
  () => props.initialData,
  (newData) => {
    if (newData) {
      setValues({
        sku: newData.sku,
        name: newData.name,
        description: newData.description || "",
        category: newData.category || "",
        price: newData.price,
        currency: newData.currency,
        unitOfMeasure: newData.unitOfMeasure,
      });
    } else {
      resetForm();
    }
  },
  { immediate: true },
);

// Watch open state to reset form when closing
watch(
  () => props.isOpen,
  (isOpen) => {
    if (!isOpen) {
      if (!props.initialData) resetForm();
    }
  },
);

const onSubmit = handleSubmit((values: CreateProductCommand) => {
  emit("submit", values);
});

const handleCancel = () => {
  emit("cancel");
};
</script>

<template>
  <div v-if="isOpen" class="modal-overlay">
    <div class="modal">
      <header class="modal__header">
        <h3>
          {{
            isEditMode
              ? t("products.updateProduct")
              : t("products.createProduct")
          }}
        </h3>
        <button class="btn-close" @click="handleCancel" :disabled="loading">
          ×
        </button>
      </header>

      <form @submit.prevent="onSubmit" class="modal__body">
        <!-- SKU (Read-only in Edit Mode) -->
        <div class="form-group">
          <label>{{ t("products.productCode") }} (SKU)</label>
          <input
            v-model="sku"
            type="text"
            :class="{ 'is-invalid': errors.sku }"
            placeholder="e.g. APPLE-15"
            :disabled="isEditMode"
          />
          <span class="error-text">{{ errors.sku }}</span>
        </div>

        <!-- Name -->
        <div class="form-group">
          <label>{{ t("products.productName") }}</label>
          <input
            v-model="name"
            type="text"
            :class="{ 'is-invalid': errors.name }"
            :placeholder="t('products.productName')"
          />
          <span class="error-text">{{ errors.name }}</span>
        </div>

        <!-- Category -->
        <div class="form-group">
          <label>{{ t("products.category") }}</label>
          <input
            v-model="category"
            type="text"
            placeholder="Electronics, Food, etc."
          />
        </div>

        <!-- Description -->
        <div class="form-group">
          <label>{{ t("products.description") }}</label>
          <textarea
            v-model="description"
            rows="3"
            :placeholder="t('products.description')"
          ></textarea>
        </div>

        <!-- Price & Unit Group -->
        <div class="form-row">
          <div class="form-group">
            <label>{{ t("products.price") }}</label>
            <input
              v-model.number="price"
              type="number"
              step="0.01"
              :class="{ 'is-invalid': errors.price }"
            />
            <span class="error-text">{{ errors.price }}</span>
          </div>

          <div class="form-group">
            <label>{{ t("common.unit") || "Unit" }}</label>
            <select
              v-model="unitOfMeasure"
              :class="{ 'is-invalid': errors.unitOfMeasure }"
            >
              <option value="PIECE">Piece</option>
              <option value="BOX">Box</option>
              <option value="KG">Kilogram</option>
              <option value="LITER">Liter</option>
              <option value="METER">Meter</option>
            </select>
            <span class="error-text">{{ errors.unitOfMeasure }}</span>
          </div>
        </div>
      </form>

      <footer class="modal__footer">
        <button
          type="button"
          class="btn btn--secondary"
          @click="handleCancel"
          :disabled="loading"
        >
          {{ t("common.cancel") }}
        </button>
        <button
          type="button"
          class="btn btn--primary"
          @click="onSubmit"
          :disabled="loading"
        >
          <span v-if="loading" class="spinner"></span>
          {{ isEditMode ? t("common.update") : t("common.create") }}
        </button>
      </footer>
    </div>
  </div>
</template>

<style scoped>
/* Reusing Modal Styles from UserDialog - Should extract to CommonModal later */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease-out;
}

.modal {
  background: var(--color-card);
  border-radius: 12px;
  width: 100%;
  max-width: 500px;
  box-shadow:
    0 20px 25px -5px rgba(0, 0, 0, 0.1),
    0 10px 10px -5px rgba(0, 0, 0, 0.04);
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

.modal__header {
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid var(--color-border);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal__header h3 {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--color-text-primary);
}

.btn-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.modal__body {
  padding: 1.5rem;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.modal__footer {
  padding: 1.25rem 1.5rem;
  border-top: 1px solid var(--color-border);
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  background-color: var(--color-surface);
  border-bottom-left-radius: 12px;
  border-bottom-right-radius: 12px;
}

/* Form Styles */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
}

.form-group label {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-text-secondary);
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 0.625rem 0.875rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 0.9375rem;
  transition: all 0.2s;
  background: var(--color-bg);
  color: var(--color-text-primary);
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  border-color: #2563eb; /* Use var if available */
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
  outline: none;
}

.form-group input:disabled {
  background-color: var(--color-surface);
  cursor: not-allowed;
}

.is-invalid {
  border-color: #dc2626 !important;
}

.error-text {
  font-size: 0.75rem;
  color: #dc2626;
}

.form-row {
  display: flex;
  gap: 1rem;
}

.form-row .form-group {
  flex: 1;
}

/* Button Styles */
.btn {
  padding: 0.625rem 1rem;
  border-radius: 6px;
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  border: none;
}

.btn--primary {
  background-color: #2563eb;
  color: white;
}

.btn--primary:hover {
  background-color: #1d4ed8;
}

.btn--primary:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

.btn--secondary {
  background-color: transparent;
  color: var(--color-text-secondary);
  border: 1px solid var(--color-border);
}

.btn--secondary:hover {
  background-color: var(--color-surface);
  color: var(--color-text-primary);
}

.spinner {
  width: 1rem;
  height: 1rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>
