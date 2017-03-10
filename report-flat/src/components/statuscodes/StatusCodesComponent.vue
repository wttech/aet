<template>
  <div>
    <div v-for="comparator in model.comparators">
      <StatusCodesResultComponent :model="comparator" :options="options"/>
    </div>
  </div>
</template>

<script>
  import StatusCodesResultComponent from './StatusCodesResultComponent';
  export default {
    name: 'StatusCodesComponent',
    components: {
      StatusCodesResultComponent
    },
    props: {
      model: Object,
      options: Object,
    }
  };
</script>

<style scoped>
</style>
