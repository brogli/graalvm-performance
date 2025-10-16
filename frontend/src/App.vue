<script setup lang="ts">
import Chart from 'primevue/chart';


import {ref, onMounted, onUnmounted, computed} from "vue";

onMounted(() => {
  chartData.value = setChartData();
  chartOptions.value = setChartOptions();
  intervalId = setInterval(fetchData, 1000);
});

let intervalId: number | null = null;

const chartData = ref();
const chartOptions = ref();
const requestsPerSecondData = ref<number[]>([]);
const labels = ref<string[]>([]);

// TODO: poll metrics (e.g. req per second), start stop form with req/s as param

const setChartData = () => {
  const documentStyle = getComputedStyle(document.documentElement);


  return {
    labels: labels,
    datasets: [
      {
        label: 'First Dataset',
        data: requestsPerSecondData,
        fill: false,
        borderColor: documentStyle.getPropertyValue('--p-cyan-500'),
        tension: 0.4
      },
      // {
      //   label: 'Second Dataset',
      //   data: [28, 48, 40, 19, 86, 27, 90],
      //   fill: false,
      //   borderColor: documentStyle.getPropertyValue('--p-gray-500'),
      //   tension: 0.4
      // }
    ]
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

async function fetchData(): Promise<void> {
  const url = "http://localhost:8082/actuator/metrics/api_requests_per_second";
  try {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }

    const result = await response.json();
    const resultValue = result.measurements[0].value;
    const newData = [...requestsPerSecondData.value, resultValue];
    if (newData.length > 20) {
      newData.shift();
    }

    // Update labels reactively
    labels.value = newData.map((_, index, arr) => {
      const secondsAgo = arr.length - 1 - index;
      return secondsAgo === 0 ? 'now' : `-${secondsAgo}s`;
    });
    requestsPerSecondData.value = newData;

  } catch (error) {
    console.error('Error fetching data:', error instanceof Error ? error.message : 'Unknown error');
  }
}

onUnmounted(() => {
  if (intervalId) {
    clearInterval(intervalId);
  }
});

</script>

<template>
  <div class="card">
    <Chart type="line" :data="chartData" :options="chartOptions" class="h-[30rem]" />
  </div>
</template>

<style scoped></style>
