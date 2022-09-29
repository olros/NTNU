<template>
  <div class="log">
    <p v-for="item in log" :key="item">{{ item }}</p>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'CalcLog',
  props: {
    log: {
      type: Array,
      required: true,
    },
  },
});
</script>

<style scoped>

</style>
