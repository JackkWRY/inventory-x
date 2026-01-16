<script setup lang="ts">
/**
 * SkeletonLoader - Reusable skeleton/shimmer loading placeholder.
 *
 * USE CASE: Show loading state for content before data arrives
 *
 * PROPS:
 * - variant: Type of skeleton (line, circle, rectangle)
 * - width: CSS width value
 * - height: CSS height value
 * - animated: Enable shimmer animation
 *
 * @example
 * ```vue
 * <SkeletonLoader variant="line" width="100%" height="20px" />
 * <SkeletonLoader variant="circle" width="40px" height="40px" />
 * ```
 */

interface Props {
  variant?: 'line' | 'circle' | 'rectangle'
  width?: string
  height?: string
  animated?: boolean
}

withDefaults(defineProps<Props>(), {
  variant: 'line',
  width: '100%',
  height: '16px',
  animated: true
})
</script>

<template>
  <div
    class="skeleton"
    :class="[
      `skeleton--${variant}`,
      { 'skeleton--animated': animated }
    ]"
    :style="{ width, height }"
    aria-hidden="true"
    role="presentation"
  />
</template>

<style scoped>
.skeleton {
  background: var(--skeleton-bg, rgba(128, 128, 128, 0.15));
  border-radius: 4px;
}

.skeleton--circle {
  border-radius: 50%;
}

.skeleton--rectangle {
  border-radius: 8px;
}

.skeleton--animated {
  background: linear-gradient(
    90deg,
    var(--skeleton-bg, rgba(128, 128, 128, 0.15)) 25%,
    var(--skeleton-highlight, rgba(128, 128, 128, 0.25)) 50%,
    var(--skeleton-bg, rgba(128, 128, 128, 0.15)) 75%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* Dark mode */
@media (prefers-color-scheme: dark) {
  .skeleton {
    --skeleton-bg: rgba(255, 255, 255, 0.08);
    --skeleton-highlight: rgba(255, 255, 255, 0.15);
  }
}

/* Also support data-theme attribute */
[data-theme="dark"] .skeleton {
  --skeleton-bg: rgba(255, 255, 255, 0.08);
  --skeleton-highlight: rgba(255, 255, 255, 0.15);
}
</style>
