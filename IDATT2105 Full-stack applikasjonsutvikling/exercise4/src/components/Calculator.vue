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
    const inputNumber = ref("");
    const showResult = ref(false);
    const hasInput = computed(
      () => inputNumber.value !== "" && inputNumber.value !== "0"
    );
    const input = computed(
      () =>
        `${inputArray.value.join(" ")} ${inputNumber.value} ${
          showResult.value ? result.value : ""
        }`
    );
    const result = computed(() => {
      let array = [...inputArray.value];
      while (array.length >= 3) {
        const sum = calc(Number(array[0]), array[1] as Sign, Number(array[2]));
        array = [sum, ...array.slice(3)];
      }
      return `= ${array[0]}`;
    });

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

    const calc = (number1: number, sign: Sign, number2: number): number => {
      if (sign === "+") {
        return number1 + number2;
      } else if (sign === "-") {
        return number1 - number2;
      } else if (sign === "/") {
        return number1 / number2;
      } else if (sign === "*") {
        return number1 * number2;
      }
      return 0;
    };

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

    const compute = () => {
      if (!hasInput.value) {
        return;
      }
      inputArray.value.push(inputNumber.value);
      inputNumber.value = "";
      showResult.value = true;
      log.value = [input.value, ...log.value];
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
