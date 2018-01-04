<template>
  <div>
    <div v-for="comparator in model.comparators">
      <JsErrorsResultComponent :model="comparator" :options="options"/>
    </div>
  </div>
</template>

<script>
  import JsErrorsResultComponent from './JsErrorsResultComponent';
  export default {
    name: 'JsErrorsComponent',
    components: {
      JsErrorsResultComponent
    },
    props: {
      model: Object,
      options: Object,
    }
  };
</script>

<style scoped>
</style>
