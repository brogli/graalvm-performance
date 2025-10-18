<script setup lang="ts">
import LineChart from "@/LineChart.vue";
import Button from 'primevue/button';
import {ref} from "vue";
import InputNumber from 'primevue/inputnumber';
import FloatLabel from 'primevue/floatlabel';

const fixedRateMs = ref<number>(100);

async function startSending(): Promise<void> {
  const url = "http://localhost:8081/start";

  const options = {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ intervalMs: fixedRateMs.value }),
  }

  try {
    const response = await fetch(url, options);
    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }

  } catch (error) {
    console.error('Error starting:', error instanceof Error ? error.message : 'Unknown error');
  }
}

async function stopSending(): Promise<void> {
  const url = "http://localhost:8081/stop";

  const options = {
    method: "POST",
  }

  try {
    const response = await fetch(url, options);
    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }

  } catch (error) {
    console.error('Error stopping:', error instanceof Error ? error.message : 'Unknown error');
  }
}

</script>

<template>
<main class="flex flex-col">
  <section class="flex gap-4">

    <FloatLabel variant="in">
      <InputNumber v-model="fixedRateMs" inputId="integeronly" id="fixedRateMs" />
      <label for="fixedRateMs">ms between Requests</label>
    </FloatLabel>
    <Button label="Start Requests" @click="startSending" />
    <Button label="Stop Requests" @click="stopSending" ></Button>


  </section>
  <LineChart/>
</main>
</template>

<style scoped></style>
