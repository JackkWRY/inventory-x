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

export interface CreateLocationCommand {
  name: string;
  type: LocationType;
  description?: string;
  address?: string;
}

export interface UpdateLocationCommand {
  name: string;
  type?: LocationType;
  description?: string;
  address?: string;
  status?: LocationStatus;
}
