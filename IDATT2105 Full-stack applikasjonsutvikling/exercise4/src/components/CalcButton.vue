<template>
  <button :disabled="disabled" class="button">
    {{ label }}
  </button>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'CalcButton',
  props: {
    label: {
      type: String,
      required: true,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
});
</script>

<style scoped>
.button {
  width: 100%;
  padding: 20px 10px;
  border: none;
  outline: none;
  background: #cccccc;
  cursor: pointer;
}
.button:hover {
  background: #c1c1c1;
}
.button:active {
  background: #bababa;
}
</style>
