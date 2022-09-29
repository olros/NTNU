<template>
  <div class="root">
    <h1>Kalkulator</h1>
    <p>Dette er en enkel kalkulator.</p>
    <div class="result">
      <h3>{{ input }}</h3>
    </div>
    <div class="grid">
        <CalcButton @click="reset" label="C"></CalcButton>
        <CalcButton @click="undo" label="DEL"></CalcButton>
    </div>
    <div class="grid">
      <div class="numbersGrid">
        <CalcButton
          v-for="num in numbers"
          @click="() => clickNumber(num)"
          :key="num"
          :disabled="numbersDisabled"
          :label="String(num)"
        ></CalcButton>
        <CalcButton :disabled="numbersDisabled" @click="() => clickNumber('.')" label="."></CalcButton>
        <CalcButton :disabled="computeDisabled" @click="compute" label="="></CalcButton>
      </div>
      <CalcButton
        v-for="sign in signs"
        @click="() => clickSign(sign)"
        :disabled="signsDisabled"
        :key="sign"
        :label="sign"
      ></CalcButton>
    </div>
    <CalcLog :log="log"></CalcLog>
  </div>
</template>

<script lang="ts">
import { computed, ref, defineComponent } from "vue";
import CalcButton from "./CalcButton.vue";
import CalcLog from "./CalcLog.vue";

type Sign = "+" | "-" | "/" | "*";
type Result = {
  value: number;
}

export default defineComponent({
  name: "Calculator",
  components: {
    CalcButton,
    CalcLog,
  },
  setup() {
    const numbers = [...Array(10).keys(), 0].slice(1);
    const signs = ["+", "-", "/", "*"];

    const log = ref<Array<string>>([]);
    const inputArray = ref<Array<string | number>>([]);
    const result = ref(0);
    const inputNumber = ref("");
    const showResult = ref(false);
    const hasInput = computed(
      () => inputNumber.value !== "" && inputNumber.value !== "0"
    );
    const input = computed(
      () =>
        `${inputArray.value.join(" ")} ${inputNumber.value} ${
          showResult.value ? `= ${result.value}` : ""
        }`
    );

    const lastInInputArr = computed(
      () => inputArray.value[inputArray.value.length - 1]
    );
    const signsDisabled = computed(
      () =>
        typeof lastInInputArr === "string" ||
        showResult.value ||
        !hasInput.value
    );
    const numbersDisabled = computed(() => showResult.value);
    const computeDisabled = computed(
      () => showResult.value || inputArray.value.length < 2 || !hasInput.value
    );

    const clickSign = (newSign: "+" | "-" | "/" | "*") => {
      if (hasInput.value) {
        inputArray.value.push(inputNumber.value);
        inputArray.value.push(newSign);
        inputNumber.value = "";
      }
    };

    const clickNumber = (newNumber: number | ".") => {
      if (inputNumber.value.includes(".") && newNumber === ".") {
        return;
      }
      if (!hasInput.value && newNumber > 0) {
        inputNumber.value = `${newNumber}`;
      } else if (hasInput.value || newNumber > 0) {
        inputNumber.value += `${newNumber}`;
      }
    };

    const compute = async () => {
      if (!hasInput.value) {
        return;
      }
      inputArray.value.push(inputNumber.value);
      inputNumber.value = "";
      const response = await fetch('http://localhost:8000/api/calculator/', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          numbers: [...inputArray.value],
        })
      });
      if (response.ok) {
        const answer: Result = await response.json();
        result.value = answer.value;
        showResult.value = true;
        log.value = [input.value, ...log.value];
      } else {
        const answer = await response.json();
        alert(answer.message);
      }
    };

    const reset = () => {
      inputArray.value = [];
      inputNumber.value = "";
      showResult.value = false;
    };
    const undo = () => {
      if (hasInput.value) {
        inputNumber.value = "";
      } else {
        inputArray.value.pop();
      }
    };

    return {
      log,
      input,
      numbers,
      signs,
      clickSign,
      clickNumber,
      compute,
      signsDisabled,
      numbersDisabled,
      computeDisabled,
      reset,
      undo,
    };
  },
});
</script>

<style scoped>
.root {
  max-width: 500px;
  width: 100%;
  margin: 0 auto;
}
.result {
  text-align: center;
  padding: 5px 10px;
  background: #333;
  color: white;
  margin-bottom: 10px;
}
.grid {
  display: grid;
  grid-template-columns: 3fr 1fr;
  grid-gap: 10px;
  margin-bottom: 10px;
}
.numbersGrid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  grid-row: span 4;
  grid-gap: 10px;
}
</style>
