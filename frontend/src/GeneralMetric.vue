<script setup lang="ts">

import {onMounted, ref} from "vue";

const metric = ref<number>();

const props = withDefaults(defineProps<{
    url: string,
    label: string,
    valueConverter?: (value: number) => number;
  }>(),
  {
    valueConverter: (value: number) => value
  })

async function fetchData(url: string): Promise<number> {
  try {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }

    const result = await response.json();
    return props.valueConverter(result.measurements[0].value);
  } catch (error) {
    console.error('Error fetching data:', error instanceof Error ? error.message : 'Unknown error');
  }

  return 0;
}

onMounted(async () => {
  metric.value = await fetchData(props.url);
})
</script>

<template>
  <section>
    <p>{{ label }}: {{ metric }}</p>
  </section>
</template>

<style scoped>

</style>
