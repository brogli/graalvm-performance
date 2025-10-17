<script setup lang="ts">
import LineChart from "@/LineChart.vue";
import Button from 'primevue/button';
import {ref} from "vue";
import InputText from 'primevue/inputtext';

const fixedRateMs = ref<string>("100");

async function startSending(): Promise<void> {
  const url = "http://localhost:8081/start";

  const options = {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ intervalMs: parseInt(fixedRateMs.value) }),
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
    <InputText type="text" v-model="fixedRateMs" />
    <Button label="Start Requests" @click="startSending" />
    <Button label="Stop Requests" @click="stopSending" ></Button>


  </section>
  <LineChart/>
</main>
</template>

<style scoped></style>
