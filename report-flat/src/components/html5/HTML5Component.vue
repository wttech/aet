<template>
  <div>
    <div v-for="comparator in model.comparators">
      <HTML5ResultComponent :model="comparator" :options="options"/>
    </div>
  </div>
</template>

<script>
  import HTML5ResultComponent from './HTML5ResultComponent';
  export default {
    name: 'HTML5Component',
    components: {
      HTML5ResultComponent
    },
    props: {
      model: Object,
      options: Object
    }
  };
</script>

<style scoped>
</style>
