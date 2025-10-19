<script setup lang="ts">
import LineChart from "@/LineChart.vue";
import Button from 'primevue/button';
import {ref} from "vue";
import InputNumber from 'primevue/inputnumber';
import FloatLabel from 'primevue/floatlabel';
import GeneralMetric from "@/GeneralMetric.vue";

const targetRequestsPerSecond = ref<number>(10);

async function startSending(): Promise<void> {
  const url = "http://localhost:8081/start";

  const options = {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({targetRequestsPerSecond: targetRequestsPerSecond.value}),
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
        <InputNumber v-model="targetRequestsPerSecond" inputId="integeronly" id="fixedRateMs"/>
        <label for="fixedRateMs">Target requests per second</label>
      </FloatLabel>
      <Button label="Start Requests" @click="startSending"/>
      <Button label="Stop Requests" @click="stopSending"></Button>


    </section>
    <div class="flex gap-4">
      <section class="flex gap-4 flex-col">
        <h1>Sender</h1>
        <ul>
          <li>
            <GeneralMetric url="http://localhost:8081/actuator/metrics/jvm.memory.max"
                           label="jvm.memory.max, MB"
                           :value-converter="(e) => Math.round(e/1000000)"/>
          </li>
          <li>
            <GeneralMetric url="http://localhost:8081/actuator/metrics/application.ready.time"
                           label="application.ready.time, seconds"
                           />
          </li>
        </ul>
        <LineChart url0="http://localhost:8081/actuator/metrics/sender_requests_sent_per_second"
                   title0="Requests sent per Second"
                    url1="http://localhost:8081/actuator/metrics/sender_responses_received_per_second"
                  title1="Responses received per second"
        :use-second-line="true" />
        <LineChart url0="http://localhost:8081/actuator/metrics/jvm.memory.used"
                   title0="jvm memory used, MB"
                   url1="http://localhost:8081/actuator/metrics/jvm.memory.committed"
                   title1="jvm memory committed, MB"
                   :use-second-line="true" :value-converter="(e) => e/1000000"/>
      </section>
      <section class="flex gap-4 flex-col">
        <h1>Receiver</h1>
        <ul>
          <li>
            <GeneralMetric url="http://localhost:8082/actuator/metrics/jvm.memory.max"
                           label="jvm.memory.max, MB"
                           :value-converter="(e) => Math.round(e/1000000)"/>
          </li>
          <li>
            <GeneralMetric url="http://localhost:8082/actuator/metrics/application.ready.time"
                           label="application.ready.time, seconds"
            />
          </li>
        </ul>
        <LineChart url0="http://localhost:8082/actuator/metrics/receiver_requests_per_second"
                   title0="Requests received per Second"/>
        <LineChart url0="http://localhost:8082/actuator/metrics/jvm.memory.used"
                   title0="jvm memory used, MB"
                   url1="http://localhost:8082/actuator/metrics/jvm.memory.committed"
                   title1="jvm memory committed, MB"
                   :use-second-line="true" :value-converter="(e) => e/1000000"/>
        <LineChart url0="http://localhost:8082/actuator/metrics/receiver_memory_mx_used"
                   title0="jvm memory used, MB"
                   url1="http://localhost:8082/actuator/metrics/receiver_memory_mx_committed"
                   title1="jvm memory committed, MB"
                   :use-second-line="true" :value-converter="(e) => e/1000000"/>
      </section>
    </div>
  </main>
</template>

<style scoped></style>
