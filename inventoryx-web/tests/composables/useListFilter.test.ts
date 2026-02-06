import { describe, it, expect } from 'vitest'
import { ref } from 'vue'
import { useListFilter } from '~/composables/useListFilter'

describe('useListFilter', () => {
  const items = [
    { id: 1, name: 'Apple', category: 'Fruit' },
    { id: 2, name: 'Banana', category: 'Fruit' },
    { id: 3, name: 'Carrot', category: 'Vegetable' }
  ]

  it('should filter items by search query', () => {
    const searchQuery = ref('')
    const { filteredItems } = useListFilter(
      ref(items), 
      searchQuery,
      (item: typeof items[0], query) => item.name.toLowerCase().includes(query)
    )

    searchQuery.value = 'app'
    expect(filteredItems.value).toHaveLength(1)
    expect(filteredItems.value[0].name).toBe('Apple')
  })
})
