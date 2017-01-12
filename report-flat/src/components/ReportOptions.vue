<template>
  <div class="ReportOptions">

    <input type="checkbox" id="showPattern" v-model="options.showPattern">
    <label for="showPattern">showPattern: {{ options.showPattern }}</label>

    <input type="checkbox" id="showCollected" v-model="options.showCollected">
    <label for="showCollected">showCollected: {{ options.showCollected }}</label>

    <input type="checkbox" id="showMask" v-model="options.showMask">
    <label for="showMask">showMask: {{ options.showMask }}</label>

  </div>
</template>

<script>
export default {
  name: 'ReportOptions',
  props: {
    options: Object

  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
