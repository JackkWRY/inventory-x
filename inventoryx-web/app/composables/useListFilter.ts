export function useListFilter<T>(
  items: Ref<T[]>,
  searchQuery: Ref<string>,
  searchPredicate: (item: T, query: string) => boolean
) {
  const filteredItems = computed(() => {
    const query = searchQuery.value.toLowerCase().trim();
    if (!query) return items.value;

    return items.value.filter((item) => searchPredicate(item, query));
  });

  return {
    filteredItems,
  };
}
