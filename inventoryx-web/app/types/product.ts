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

export interface CreateProductCommand {
  sku: string;
  name: string;
  description?: string;
  category?: string;
  price: number;
  currency: string;
  unitOfMeasure: string;
}

export interface UpdateProductCommand {
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
