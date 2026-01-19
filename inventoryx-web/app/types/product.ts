export interface Product {
  id: string;
  sku: string;
  name: string;
  description?: string;
  category?: string;
  price: number;
  currency: string;
  unitOfMeasure: string;
  version: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateProductRequest {
  sku: string;
  name: string;
  description?: string;
  category?: string;
  price: number;
  currency: string;
  unitOfMeasure: string;
}

export interface UpdateProductRequest {
  name: string;
  description?: string;
  category?: string;
  price: number;
  currency: string;
  unitOfMeasure: string;
}

export interface ProductPageResponse {
  content: Product[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}
