<script setup lang="ts">

import Chart from "primevue/chart";
import {onMounted, onUnmounted, type Ref, ref, watch} from "vue";
import {useIntervalFn} from '@vueuse/core'

const props = withDefaults(defineProps<{
    useSecondLine?: boolean
    useThirdLine?: boolean
    url0: string,
    url1?: string,
    url2?: string
    title0: string,
    title1?: string,
    title2?: string
    valueConverter?: (value: number) => number;
    shouldPollMetrics: boolean
  }>(),
  {
    valueConverter: (value: number) => value
  }
)

watch(() => props.shouldPollMetrics, (value) => {
  if (value) {
    resume()
  } else {
    pause()
  }
})

onMounted(() => {
  chartData.value = setChartData();
  chartOptions.value = setChartOptions();
  pause();
});


const {pause, resume, isActive} = useIntervalFn(() => {
  fetchData(data0, props.url0);
  if (props.useSecondLine && props.url1) {
    fetchData(data1, props.url1)
  }
  if (props.useThirdLine && props.url2) {
    fetchData(data2, props.url2)
  }
}, 1000)


function stopUpdatingMetrics(): void {
  if (isActive) {
    pause();
  }
}


const chartData = ref();
const chartOptions = ref();
const data0 = ref<number[]>([]);
const data1 = ref<number[]>([]);
const data2 = ref<number[]>([]);
const labels = ref<string[]>([]);


const setChartData = () => {
  const documentStyle = getComputedStyle(document.documentElement);

  const dataset0 = {
    label: props.title0,
    data: data0,
    fill: false,
    borderColor: documentStyle.getPropertyValue('--p-cyan-500'),
    tension: 0.4
  };

  const dataset1 = {
    label: props.title1 || "",
    data: data1,
    fill: false,
    borderColor: documentStyle.getPropertyValue('--p-gray-500'),
    tension: 0.4
  }

  const dataset2 = {
    label: props.title2 || "",
    data: data2,
    fill: false,
    borderColor: documentStyle.getPropertyValue('--p-red-500'),
    tension: 0.4
  }

  const datasets = [dataset0]
  if (props.useSecondLine) {
    datasets.push(dataset1)
  }
  if (props.useThirdLine) {
    datasets.push(dataset2)
  }

  return {
    labels: labels,
    datasets: datasets
  };
};
const setChartOptions = () => {
  const documentStyle = getComputedStyle(document.documentElement);
  const textColor = documentStyle.getPropertyValue('--p-text-color');
  const textColorSecondary = documentStyle.getPropertyValue('--p-text-muted-color');
  const surfaceBorder = documentStyle.getPropertyValue('--p-content-border-color');

  return {
    maintainAspectRatio: false,
    aspectRatio: 0.6,
    animation: false,
    plugins: {
      legend: {
        labels: {
          color: textColor
        }
      }
    },
    scales: {
      x: {
        ticks: {
          color: textColorSecondary
        },
        grid: {
          color: surfaceBorder
        }
      },
      y: {
        ticks: {
          color: textColorSecondary
        },
        grid: {
          color: surfaceBorder
        }
      }
    }
  };
}

async function fetchData(data: Ref<number[]>, url: string): Promise<void> {
  try {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }

    const result = await response.json();
    const resultValue = props.valueConverter(result.measurements[0].value);
    const newData = [...data.value, resultValue];
    if (newData.length > 20) {
      newData.shift();
    }

    // Update labels reactively
    labels.value = newData.map((_, index, arr) => {
      const secondsAgo = arr.length - 1 - index;
      return secondsAgo === 0 ? 'now' : `-${secondsAgo}s`;
    });
    data.value = newData;

  } catch (error) {
    console.error('Error fetching data:', error instanceof Error ? error.message : 'Unknown error');
  }
}

onUnmounted(() => {
  stopUpdatingMetrics()
});

</script>

<template>
  <section class="flex flex-col h-[20rem]">
    <Chart type="line" :data="chartData" :options="chartOptions" class="flex grow"/>
  </section>
</template>

<style scoped>

</style>
