export interface Location {
  id: string;
  name: string;
  type: LocationType;
  description?: string;
  address?: string;
  status: LocationStatus;
  createdAt: string;
  updatedAt: string;
}

export enum LocationType {
  WAREHOUSE = "WAREHOUSE",
  STORE = "STORE",
  TRANSIT = "TRANSIT",
}

export enum LocationStatus {
  ACTIVE = "ACTIVE",
  INACTIVE = "INACTIVE",
}

export interface CreateLocationRequest {
  name: string;
  type: LocationType;
  description?: string;
  address?: string;
}

export interface UpdateLocationRequest {
  name: string;
  type?: LocationType;
  description?: string;
  address?: string;
  status?: LocationStatus;
}
