<template>
  <div class="ReportOptions container">
    <div class="wrapper">
      <div class="option">
        <input type="checkbox" id="showPattern" v-model="options.showPattern">
        <label for="showPattern">Show Pattern</label>
      </div>
      <div class="option">
        <input type="checkbox" id="showCollected" v-model="options.showCollected">
        <label for="showCollected">Show Collected</label>
      </div>
      <div class="option">
        <input type="checkbox" id="showMask" v-model="options.showMask">
        <label for="showMask">Show Mask</label>
      </div>
      <div class="option">
        <input type="checkbox" id="showJS" v-model="options.showJS">
        <label for="showJS">Show JS</label>
      </div>
      <div class="option">
        <input type="checkbox" id="showScreen" v-model="options.showScreen">
        <label for="showScreen">Show Screen</label>
      </div>
      <div class="option">
        <input type="checkbox" id="showStatusCodes" v-model="options.showStatusCodes">
        <label for="showStatusCodes">Show Status Codes</label>
      </div>
    </div>
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
  .ReportOptions {
    position: fixed;
    background-color: white;
    width: 100%;
    left: 0;
    top: 0;
    z-index: 10;
  }

  .ReportOptions .wrapper {
    display: flex;
    justify-content: space-between;
  }

  .option {
    display: flex;
    align-items: center;
    padding: 10px 0;
    font-weight: bold;
  }

  @media print {
    .ReportOptions {
      display: none;
    }
  }
</style>
