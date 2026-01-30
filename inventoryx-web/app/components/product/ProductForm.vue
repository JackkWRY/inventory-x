<script setup lang="ts">
import type { CreateProductCommand, Product } from "~/types/product";
import { useForm } from "vee-validate";
import * as yup from "yup";
import BaseModal from "~/components/common/BaseModal.vue";

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
      .typeError(t("validation.positiveNumber"))
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

const dialogTitle = computed(() =>
  props.isEditMode ? t("products.updateProduct") : t("products.createProduct")
);

const onSubmit = handleSubmit((values: CreateProductCommand) => {
  emit("submit", values);
});

const handleCancel = () => {
  emit("cancel");
};
</script>

<template>
  <BaseModal
    :open="isOpen"
    :title="dialogTitle"
    size="md"
    :close-on-escape="!loading"
    :close-on-backdrop="!loading"
    @close="handleCancel"
  >
    <!-- Form Body -->
    <template #body>
      <form @submit.prevent="onSubmit" class="product-form">
        <!-- SKU (Read-only in Edit Mode) -->
        <div class="form-group">
          <label class="form-label">{{ t("products.productCode") }} (SKU)</label>
          <input
            v-model="sku"
            type="text"
            class="form-input"
            :class="{ 'is-invalid': errors.sku }"
            placeholder="e.g. APPLE-15"
            :disabled="isEditMode"
          />
          <span v-if="errors.sku" class="form-error">{{ errors.sku }}</span>
        </div>

        <!-- Name -->
        <div class="form-group">
          <label class="form-label">{{ t("products.productName") }}</label>
          <input
            v-model="name"
            type="text"
            class="form-input"
            :class="{ 'is-invalid': errors.name }"
            :placeholder="t('products.productName')"
          />
          <span v-if="errors.name" class="form-error">{{ errors.name }}</span>
        </div>

        <!-- Category -->
        <div class="form-group">
          <label class="form-label">{{ t("products.category") }}</label>
          <input
            v-model="category"
            type="text"
            class="form-input"
            placeholder="Electronics, Food, etc."
          />
        </div>

        <!-- Description -->
        <div class="form-group">
          <label class="form-label">{{ t("products.description") }}</label>
          <textarea
            v-model="description"
            class="form-input"
            rows="3"
            :placeholder="t('products.description')"
          ></textarea>
        </div>

        <!-- Price & Unit Group -->
        <div class="form-row">
          <div class="form-group">
            <label class="form-label">{{ t("products.price") }}</label>
            <input
              v-model.number="price"
              type="number"
              step="0.01"
              class="form-input"
              :class="{ 'is-invalid': errors.price }"
            />
            <span v-if="errors.price" class="form-error">{{ errors.price }}</span>
          </div>

          <div class="form-group">
            <label class="form-label">{{ t("common.unit") || "Unit" }}</label>
            <select
              v-model="unitOfMeasure"
              class="form-input"
              :class="{ 'is-invalid': errors.unitOfMeasure }"
            >
              <option value="PIECE">Piece</option>
              <option value="BOX">Box</option>
              <option value="KG">Kilogram</option>
              <option value="LITER">Liter</option>
              <option value="METER">Meter</option>
            </select>
            <span v-if="errors.unitOfMeasure" class="form-error">{{ errors.unitOfMeasure }}</span>
          </div>
        </div>
      </form>
    </template>

    <!-- Footer -->
    <template #footer>
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
    </template>
  </BaseModal>
</template>

<style scoped>
/* Form layout */
.product-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-row {
  display: flex;
  gap: 1rem;
}

.form-row .form-group {
  flex: 1;
}

/* Validation states */
.is-invalid {
  border-color: #dc2626 !important;
}

/* Spinner */
.spinner {
  width: 1rem;
  height: 1rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>

