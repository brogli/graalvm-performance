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
    body: JSON.stringify({intervalMs: fixedRateMs.value}),
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
  <main class="flex flex-col gap-4">
    <section class="flex gap-4">

      <FloatLabel variant="in">
        <InputNumber v-model="fixedRateMs" inputId="integeronly" id="fixedRateMs"/>
        <label for="fixedRateMs">ms between Requests</label>
      </FloatLabel>
      <Button label="Start Requests" @click="startSending"/>
      <Button label="Stop Requests" @click="stopSending"></Button>


    </section>
    <div class="flex gap-4">
    <section class="flex gap-4 flex-col">
      <h1>Sender</h1>
      <LineChart url0="http://localhost:8082/actuator/metrics/api_requests_per_second" title0="Requests received per Second"/>
      <LineChart url0="http://localhost:8082/actuator/metrics/api_requests_per_second" title0="Requests received per Second"/>
    </section>
    <section class="flex gap-4 flex-col">
      <h1>Receiver</h1>
      <LineChart url0="http://localhost:8082/actuator/metrics/api_requests_per_second" title0="Requests received per Second"/>
      <LineChart url0="http://localhost:8082/actuator/metrics/jvm.memory.used" title0="jvm memory used, Mb"
                 url1="http://localhost:8082/actuator/metrics/jvm.memory.committed" title1="jvm memory committed, Mb"
                 url2="http://localhost:8082/actuator/metrics/jvm.memory.max" title2="jvm memory max, Mb"
                 :use-second-line="true" :useThirdLine="true" :value-converter="(e) => e/1000000"/>
    </section>
    </div>
  </main>
</template>

<style scoped></style>
