export interface NavigationItem {
  label: string;
  to: string;
  roles?: string[]; // If undefined, accessible by authenticated users
}

export const navigationItems: NavigationItem[] = [
  {
    label: "navigation.dashboard",
    to: "/dashboard",
  },
  {
    label: "navigation.products",
    to: "/products",
  },
  {
    label: "navigation.inventory",
    to: "/inventory",
  },
  {
    label: "navigation.warehouses",
    to: "/locations",
  },
  {
    label: "navigation.users",
    to: "/users",
    roles: ["ADMIN"],
  },
];
